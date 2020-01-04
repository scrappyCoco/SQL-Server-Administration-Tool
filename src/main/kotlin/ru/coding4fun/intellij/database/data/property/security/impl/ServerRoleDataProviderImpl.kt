package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.security.ServerRoleDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.property.security.MsServerRoleModel
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.function.Consumer

class ServerRoleDataProviderImpl(project: Project) : MsClient(project), ServerRoleDataProvider {
	override fun getModel(objectId: String?, consumer: Consumer<MsServerRoleModel>) {
		val model = MsServerRoleModel()
		val queries = arrayListOf(
			QueryDefinition(
				"sql/security/detail/BuiltinPermissions.sql",
				DataProviderMessages.message("security.server.role.progress.builtin"),
				Consumer { model.builtin = it.getObjects() }),
			QueryDefinition(
				"sql/action/property/security/login/ServerPermissions.sql",
				DataProviderMessages.message("security.login.progress.server.permission"),
				Consumer { model.serverPermissions = it.getObjects() },
				hashMapOf("loginId" to (objectId ?: "NULL"))
			), QueryDefinition(
				"sql/action/property/security/login/Securables2.sql",
				DataProviderMessages.message("security.server.role.progress.securable"),
				Consumer { model.securables = it.getObjects() },
				hashMapOf("loginId" to (objectId ?: "NULL"))
			)
		)

		if (objectId == null) {
			model.securables = emptyList()
			model.role = ModelModification(null, null)

			queries.add(QueryDefinition("sql/action/property/security/role/AllMemberships.sql",
				DataProviderMessages.message("security.server.role.progress.membership"),
				Consumer { model.memberships = it.getObjects() }
			))

			queries.add(
				QueryDefinition("sql/action/property/security/role/AllMembers.sql",
					DataProviderMessages.message("security.server.role.progress.member"),
					Consumer { model.members = it.getObjects() })
			)
		} else {
			queries.add(
				QueryDefinition(
					"sql/action/property/security/role/Role.sql",
					DataProviderMessages.message("security.server.role.progress.main"),
					Consumer { model.role = it.getModObject() },
					hashMapOf("roleId" to objectId)
				)
			)

			queries.add(
				QueryDefinition(
					"sql/action/property/security/role/Memberships.sql",
					DataProviderMessages.message("security.server.role.progress.membership"),
					Consumer { model.memberships = it.getObjects() },
					hashMapOf("roleId" to objectId)
				)
			)

			queries.add(
				QueryDefinition(
					"sql/action/property/security/role/Members.sql",
					DataProviderMessages.message("security.server.role.progress.member"),
					Consumer { model.members = it.getObjects() },
					hashMapOf("roleId" to objectId)
				)
			)
		}

		invokeComposite(DataProviderMessages.message("security.server.role.progress.task"), queries, Consumer { consumer.accept(model) })
	}

	override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsServerRoleModel>>,
        errorConsumer: Consumer<Exception>
    ) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}