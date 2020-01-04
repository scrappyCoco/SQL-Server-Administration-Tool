package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.security.LoginDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.BuiltinPermission
import ru.coding4fun.intellij.database.model.property.security.login.*
import ru.coding4fun.intellij.database.model.property.security.role.RoleMember
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.function.Consumer

class LoginDataProviderImpl(project: Project) : MsClient(project),
	LoginDataProvider {
	override fun getModel(objectId: String?, consumer: Consumer<MsLoginModel>) {
		val model = MsLoginModel()

		val queries = arrayListOf(QueryDefinition(
			"sql/common/Language.sql",
			DataProviderMessages.message("security.login.progress.lang"),
			Consumer { model.languages = it.getObjects() }
		), QueryDefinition(
			"sql/common/Database.sql",
			DataProviderMessages.message("security.login.progress.database"),
			Consumer { model.databases = it.getObjects() }
		), QueryDefinition(
			"sql/tree/security/Credential.sql",
			DataProviderMessages.message("security.login.progress.credential"),
			Consumer { model.credentials = it.getObjects() }
		), QueryDefinition(
			"sql/tree/security/AsymmetricKey.sql",
			DataProviderMessages.message("security.login.progress.asymmetric.key"),
			Consumer { model.asymmetricKeys = it.getObjects() }
		), QueryDefinition(
			"sql/tree/security/Certificate.sql",
			DataProviderMessages.message("security.login.progress.certificate"),
			Consumer { model.certificates = it.getObjects() }
		), QueryDefinition(
			"sql/action/property/security/login/ServerPermissions.sql",
			DataProviderMessages.message("security.login.progress.server.permission"),
			Consumer { model.serverPermissions = it.getObjects() },
			hashMapOf("loginId" to (objectId ?: "NULL"))
		), QueryDefinition(
			"sql/action/property/security/login/Securables2.sql",
			DataProviderMessages.message("security.login.progress.securable"),
			Consumer { model.securables = it.getObjects() },
			hashMapOf("loginId" to (objectId ?: "NULL"))
		), QueryDefinition(
			"sql/security/detail/BuiltinPermissions.sql",
			DataProviderMessages.message("security.login.progress.builtin.permission"),
			Consumer { model.builtInPermission = it.getObjects() }
		), QueryDefinition(
			"sql/action/property/security/login/DatabaseRoles.sql",
			DataProviderMessages.message("security.login.progress.database.role"),
			Consumer { model.dbRoles = it.getObjects() },
			hashMapOf("serverPrincipalId" to (objectId ?: "NULL"))
		)
		)

		if (objectId != null) {
			queries.add(
				QueryDefinition(
					"sql/action/property/security/login/MainInfo.sql",
					DataProviderMessages.message("security.login.progress.main"),
					Consumer { model.login = it.getModObject() },
					hashMapOf("principalId" to objectId)
				)
			)

			queries.add(
				QueryDefinition(
					"sql/security/detail/ServerRolesOfMemberExtended.sql",
					DataProviderMessages.message("security.login.progress.server.role"),
					Consumer { model.serverRoles = it.getObjects() },
					hashMapOf("serverPrincipalId" to objectId)
				)
			)

			queries.add(
				QueryDefinition(
					"sql/action/property/security/login/Databases.sql",
					DataProviderMessages.message("security.login.progress.database"),
					Consumer { model.loginDatabases = it.getObjects() },
					hashMapOf("serverPrincipalId" to objectId)
				)
			)
		} else {
			model.login = ModelModification(null, null)

			queries.add(QueryDefinition(
				"sql/security/detail/AllServerRoles.sql",
				DataProviderMessages.message("security.login.progress.server.role"),
				Consumer { model.serverRoles = it.getObjects() }
			))

			queries.add(QueryDefinition(
				"sql/action/property/security/login/DatabasesDefault.sql",
				DataProviderMessages.message("security.login.progress.database"),
				Consumer { model.loginDatabases = it.getObjects() }
			))
		}

		invokeComposite(
			DataProviderMessages.message("security.login.progress.task"),
			queries,
			Consumer { consumer.accept(model) })
	}

	override fun getModels(
		objectIds: Array<String>?,
		successConsumer: Consumer<Map<String, MsLoginModel>>,
		errorConsumer: Consumer<Exception>
	) {
		val models = objectIds!!.associate { it to MsLoginModel()  }
		var languages: List<BasicIdentity> = emptyList()
		var certificates: List<BasicIdentity> = emptyList()
		var asymmetricKeys: List<BasicIdentity> = emptyList()
		var credentials: List<BasicIdentity> = emptyList()
		var databases: List<BasicIdentity> = emptyList()
		var serverPermissions: List<MsServerPermission> = emptyList()
		var builtInPermission: List<BuiltinPermission> = emptyList()
		var securables: List<MsSecurable> = emptyList()
		var dbRoles: List<MsDatabaseRoleMembership> = emptyList()
		var mains: List<MsLogin> = emptyList()
		var serverRoles: List<RoleMember> = emptyList()

		val queries = arrayListOf(QueryDefinition(
			"sql/common/Language.sql",
			DataProviderMessages.message("security.login.progress.lang"),
			Consumer { languages = it.getObjects() }
		), QueryDefinition(
			"sql/common/Database.sql",
			DataProviderMessages.message("security.login.progress.database"),
			Consumer { databases = it.getObjects() }
		), QueryDefinition(
			"sql/tree/security/Credential.sql",
			DataProviderMessages.message("security.login.progress.credential"),
			Consumer { credentials = it.getObjects() }
		), QueryDefinition(
			"sql/tree/security/AsymmetricKey.sql",
			DataProviderMessages.message("security.login.progress.asymmetric.key"),
			Consumer { asymmetricKeys = it.getObjects() }
		), QueryDefinition(
			"sql/tree/security/Certificate.sql",
			DataProviderMessages.message("security.login.progress.certificate"),
			Consumer { certificates = it.getObjects() }
		), QueryDefinition(
			"sql/action/property/security/login/ServerPermissions.sql",
			DataProviderMessages.message("security.login.progress.server.permission"),
			Consumer { serverPermissions = it.getObjects() }
		), QueryDefinition(
			"sql/action/property/security/login/Securables2.sql",
			DataProviderMessages.message("security.login.progress.securable"),
			Consumer { securables = it.getObjects() }
		), QueryDefinition(
			"sql/security/detail/BuiltinPermissions.sql",
			DataProviderMessages.message("security.login.progress.builtin.permission"),
			Consumer { builtInPermission = it.getObjects() }
		), QueryDefinition(
			"sql/action/property/security/login/DatabaseRoles.sql",
			DataProviderMessages.message("security.login.progress.database.role"),
			Consumer { dbRoles = it.getObjects() }
		), QueryDefinition(
			"sql/action/property/security/login/MainInfo.sql",
			DataProviderMessages.message("security.login.progress.main"),
			Consumer { mains = it.getObjects() }
		), QueryDefinition(
			"sql/security/detail/ServerRolesOfMemberExtended.sql",
			DataProviderMessages.message("security.login.progress.server.role"),
			Consumer { serverRoles = it.getObjects() }
		))

//		if (objectId != null) {
//			queries.add(
//				QueryDefinition(
//					"sql/action/property/security/login/Databases.sql",
//					DataProviderMessages.message("security.login.progress.database"),
//					Consumer { model.loginDatabases = it.getObjects() },
//					hashMapOf("serverPrincipalId" to objectId)
//				)
//			)
//		} else {
//			queries.add(QueryDefinition(
//				"sql/action/property/security/login/DatabasesDefault.sql",
//				DataProviderMessages.message("security.login.progress.database"),
//				Consumer { model.loginDatabases = it.getObjects() }
//			))
//		}

		invokeComposite(
			DataProviderMessages.message("security.login.progress.task"),
			queries,
			Consumer {
				val serverPermissionMap = serverPermissions.groupBy { it.principalId }
				val securableMap = securables.groupBy { it.principalId }
				val dbRoleMap = dbRoles.groupBy { it.principalId }
				val mainMap = mains.associateBy { it.id }

			for (modelEntry in models) {
					val model = modelEntry.value
					val modelId = modelEntry.key

					model.builtInPermission = builtInPermission
					model.languages = languages
					model.certificates = certificates
					model.asymmetricKeys = asymmetricKeys
					model.credentials = credentials
					model.databases = databases
					model.serverPermissions = serverPermissionMap[modelId] ?: emptyList()
					model.securables = securableMap[modelId] ?: emptyList()
					model.dbRoles = dbRoleMap[modelId] ?: emptyList()
					model.login = ModelModification(mainMap[modelId], null)
					model.loginDatabases = emptyList()
				}
				successConsumer.accept(models)
			}, errorConsumer)
	}
}