package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.security.LoginDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.property.security.login.MsLoginModel
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
}