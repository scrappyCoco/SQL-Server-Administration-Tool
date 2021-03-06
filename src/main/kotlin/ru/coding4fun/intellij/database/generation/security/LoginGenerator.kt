package ru.coding4fun.intellij.database.generation.security

import ru.coding4fun.intellij.database.extension.*
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.security.login.MsDatabaseOfLogin
import ru.coding4fun.intellij.database.model.property.security.login.MsDatabaseRoleMembership
import ru.coding4fun.intellij.database.model.property.security.login.MsLogin
import ru.coding4fun.intellij.database.model.property.security.login.MsLoginModel
import ru.coding4fun.intellij.database.model.property.security.role.RoleMember
import ru.coding4fun.intellij.database.model.tree.MsKind
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.Modifications

object LoginGenerator : ScriptGeneratorBase<MsLoginModel>() {
	private fun appendDatabases(scriptBuilder: StringBuilder, databaseModifications: Modifications<MsDatabaseOfLogin>, loginName: String) {
		if (!databaseModifications.any()) return

		val modifiedDatabases = databaseModifications.filter { it.isModified }.toList()

		if (!modifiedDatabases.any()) return

		scriptBuilder.appendLnIfAbsent()
		scriptBuilder.append("-- region Databases of ", loginName)
		scriptBuilder.appendJbLn()

		for (databaseModification in modifiedDatabases) {
			val newDb = databaseModification.new!!
			val isUserDrop = !newDb.isSelected && databaseModification.old?.isSelected ?: false

			scriptBuilder.append("USE [", newDb.name, "]")
			scriptBuilder.appendJbLn()

			val newUserName = newDb.user ?: loginName

			if (isUserDrop) {
				scriptBuilder.append("DROP USER [", newUserName, "];")
				scriptBuilder.appendJbLn()
			} else {
				val isUserCreated =
					newDb.isSelected && !(databaseModification.old?.isSelected ?: false)

				if (isUserCreated) {
					scriptBuilder.append("CREATE USER [", newUserName, "] ")
					scriptBuilder.appendJbLn()

					scriptBuilder.append("FROM LOGIN [", loginName, "]")
					scriptBuilder.appendJbLn()

					val hasDefaultSchema = !newDb.defaultSchema.isNullOrBlank()
					if (hasDefaultSchema) {
						scriptBuilder.append("WITH DEFAULT_SCHEMA = [", newDb.defaultSchema, "]")
						scriptBuilder.appendJbLn()
					}
				} else {
					scriptBuilder.append("ALTER USER [", databaseModification.old!!.user, "] ")
					scriptBuilder.appendJbLn()

					val userChanged = databaseModification.old!!.user != newDb.user
					val schemaChanged = databaseModification.old!!.defaultSchema != newDb.defaultSchema

					if (userChanged || schemaChanged) {
						scriptBuilder.append("WITH ")
							.addCommaWithNewLineScope()
							.also { separateScope ->
								separateScope.invokeIf(userChanged) {
									scriptBuilder.append("NAME = [", newUserName, "]")
								}
								separateScope.invokeIf(schemaChanged) {
									scriptBuilder.append(
										"DEFAULT_SCHEMA = [",
										newDb.defaultSchema,
										"]"
									)
								}
							}
					}
				}
			}
			scriptBuilder.appendGo()
		}

		scriptBuilder.append("-- endregion")
		scriptBuilder.appendJbLn()
	}

	private fun appendDatabaseRoles(scriptBuilder: StringBuilder, databaseRoleModifications: Modifications<MsDatabaseRoleMembership>, loginName: String) {
		if (!databaseRoleModifications.any()) return

		val modifiedDatabaseRoles = databaseRoleModifications
			.filter { it.isModified }
			.toList()

		if (!modifiedDatabaseRoles.any()) return

		scriptBuilder.appendLnIfAbsent()
		scriptBuilder.append("-- region Database roles of ", loginName)
		scriptBuilder.appendJbLn()

		val sortedDatabaseRoleModifications = databaseRoleModifications
			.sortedBy { m -> m.new!!.databaseName }
			.toList()

		var currentDatabase = ""
		for (role in sortedDatabaseRoleModifications) {
			val dbRole = role.new!!
			if (currentDatabase != dbRole.databaseName) {
				currentDatabase = dbRole.databaseName
				scriptBuilder.append("USE [", currentDatabase, "]").appendGo()
			}

			val isRoleAdded = dbRole.isSelected && !(role.old?.isSelected ?: false)
			val action = if (isRoleAdded) "ADD" else "DROP"
			scriptBuilder.append("ALTER ROLE [", dbRole.name, "] ")
			scriptBuilder.append(action, " MEMBER [", loginName, "];")
			scriptBuilder.appendJbLn()
		}
		scriptBuilder.appendGo()

		scriptBuilder.append("-- endregion")
		scriptBuilder.appendJbLn()
	}

	private fun appendServerRoles(scriptBuilder: StringBuilder, serverRoleModifications: Modifications<RoleMember>, loginName: String) {
		if (!serverRoleModifications.any()) return

		scriptBuilder.appendLnIfAbsent()
			.append("-- region Server roles of ", loginName).appendJbLn()
			.append("USE [master]")
			.appendGo()


		for (serverRoleModification in serverRoleModifications) {
			val isAdded = !(serverRoleModification.old?.isSelected ?: false) && serverRoleModification.new!!.isSelected

			scriptBuilder.append("ALTER SERVER ROLE [", serverRoleModification.new!!.name, "] ")
			scriptBuilder.append(if (isAdded) "ADD " else "DROP ")
			scriptBuilder.append("MEMBER [", loginName, "];")
			scriptBuilder.appendJbLn()
		}

		scriptBuilder.append("-- endregion")
		scriptBuilder.appendJbLn()
	}


	override fun getAlterPart(model: MsLoginModel): String? {
		val scriptBuilder = StringBuilder()
		appendOptions(scriptBuilder, model.login, false)
		val loginName = model.login.new!!.name
		appendDatabases(scriptBuilder, model.dbModifications, loginName)
		appendDatabaseRoles(scriptBuilder, model.dbRoleModifications, loginName)
		appendServerRoles(scriptBuilder, model.memberModifications, loginName)
		SecurityScriptUtil.appendServerPermissions(scriptBuilder, model.serverPermissionModifications, loginName)
		return scriptBuilder.toString()
	}

	override fun getCreatePart(model: MsLoginModel, scriptBuilder: StringBuilder, reverse: Boolean): StringBuilder {
		if (reverse) model.login.reverse()
		val login = (model.login.new ?: model.login.old)!!
		val fromSource = when (login.principalKind) {
			PrincipalKind.WINDOWS_LOGIN.toString() -> " FROM WINDOWS "
			PrincipalKind.SQL_LOGIN.toString() -> ""
			PrincipalKind.CERTIFICATE_MAPPED_LOGIN.toString() -> " FROM CERTIFICATE [${login.certificate}] "
			PrincipalKind.ASYMMETRIC_KEY_MAPPED_LOGIN.toString() -> " FROM ASYMMETRIC KEY [${login.asymmetricKey}] "
			else -> throw NotImplementedError()
		}

		appendOptions(scriptBuilder, model.login, true, fromSource)
		if (login.denyLogin) scriptBuilder.append("DENY CONNECT SQL TO [", login.name, "];").appendGo()

		appendDatabases(scriptBuilder, model.dbModifications, login.name)
		appendDatabaseRoles(scriptBuilder, model.dbRoleModifications, login.name)
		appendServerRoles(scriptBuilder, model.memberModifications, login.name)
		SecurityScriptUtil.appendServerPermissions(scriptBuilder, model.serverPermissionModifications, login.name)

		return scriptBuilder.appendJbLn()
	}

	private fun appendOptions(
		scriptBuilder: StringBuilder,
		modelModification: ModelModification<MsLogin>,
		isCreationMode: Boolean,
		fromPart: String = ""
	) {
		val login = modelModification.new!!
		val prefix =
			(if (isCreationMode) "CREATE " else "ALTER ") +
					"LOGIN [" + login.name + "]" + fromPart

		val optionsScope = scriptBuilder.addSeparatorScope("$prefix\nWITH ") {
			scriptBuilder.append(",").appendJbLn().append("    ")
		}
			.also { separateScope ->
				if (MsKind.SQL_LOGIN.toString() == login.principalKind) {
					var passwordAdded = false

					separateScope.invokeIf(isCreationMode && !login.loginPasswordHashed.isNullOrEmpty()) {
						scriptBuilder.append("PASSWORD = ", login.loginPasswordHashed!!, " HASHED")
						passwordAdded = true
					}.invokeElseIf(!login.loginPassword.isNullOrBlank()) {
						scriptBuilder.append("PASSWORD = '", login.loginPassword!!, "'")
						passwordAdded = true
					}
					if (passwordAdded && login.mustChange) {
						scriptBuilder.append(" MUST_CHANGE")
					}
					separateScope.invokeIf(passwordAdded) {
						scriptBuilder.append(
							"CHECK_POLICY = ",
							(if (login.isPolicyChecked) "ON" else "OFF")
						)
					}
					separateScope.invokeIf(passwordAdded) {
						scriptBuilder.append(
							"CHECK_EXPIRATION = ",
							(if (login.isExpirationChecked) "ON" else "OFF")
						)
					}
				}

				val isWinOrSqlOption =
					login.principalKind == MsKind.WINDOWS_LOGIN.name || login.principalKind == MsKind.SQL_LOGIN.name
				separateScope.invokeIf(isWinOrSqlOption && isCreationMode && login.sid != null) {
					scriptBuilder.append("SID = ", login.sid)
				}
				separateScope.invokeIf(isWinOrSqlOption && login.defaultDatabase != modelModification.old?.defaultDatabase) {
					val dbName = if (login.defaultDatabase.isNullOrBlank())
						"master" else login.defaultDatabase
					scriptBuilder.append("DEFAULT_DATABASE = [", dbName, "]")
				}
				separateScope.invokeIf(login.defaultLanguage != modelModification.old?.defaultLanguage) {
					val language = if (login.defaultLanguage.isNullOrBlank())
						"us_english" else login.defaultLanguage
					scriptBuilder.append("DEFAULT_LANGUAGE = [", language, "]")
				}
				separateScope.invokeIf(!login.credential.isNullOrBlank()) {
					scriptBuilder.append("CREDENTIAL = [", login.credential, "]")
				}
			}

		if (optionsScope.invokeCount == 0 && isCreationMode) scriptBuilder.append(prefix).appendGo()

		if (isCreationMode && login.isDisabled ||
			!isCreationMode && login.isDisabled != modelModification.old!!.isDisabled
		) {
			val action = if (login.isDisabled) "DISABLE" else "ENABLE"
			scriptBuilder.appendGo()
				.append("ALTER LOGIN [", login.name, "] ", action, ";")
				.appendGo()
		}
	}

	override fun getDropPart(model: MsLoginModel, scriptBuilder: StringBuilder): StringBuilder {
		return scriptBuilder.append("DROP LOGIN [", model.login.old!!.name, "];")
	}
}