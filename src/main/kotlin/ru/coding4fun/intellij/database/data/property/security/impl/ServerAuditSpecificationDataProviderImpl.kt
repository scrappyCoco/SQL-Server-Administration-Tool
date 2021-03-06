package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.security.ServerAuditSpecificationDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditSpecModel
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.function.Consumer

class ServerAuditSpecificationDataProviderImpl(project: Project) : MsClient(project),
	ServerAuditSpecificationDataProvider {
	override fun getModel(objectId: String?, consumer: Consumer<MsServerAuditSpecModel>) {
		val model = MsServerAuditSpecModel()
		val queries = arrayListOf(
			QueryDefinition(
				"sql/action/property/security/server-audit/ServerAudits.sql",
					DataProviderMessages.message("security.server.audit.specification.progress.audit"),
				Consumer { model.defaultServerAudits = it.getObjects() }),
			QueryDefinition(
				if (objectId != null)
					"sql/action/property/security/server-audit/ServerAuditSpecificationActions.sql"
				else "sql/action/property/security/server-audit/ServerAuditSpecificationActionsDefault.sql",
				DataProviderMessages.message("security.server.audit.specification.progress.action"),
				Consumer { model.actions = it.getObjects() },
				if (objectId != null) hashMapOf("specId" to objectId) else emptyMap()
			)
		)

		if (objectId != null) {
			queries.add(QueryDefinition(
				"sql/action/property/security/server-audit/ServerAuditSpecification.sql",
				DataProviderMessages.message("security.server.audit.specification.progress.main"),
				Consumer {model.spec = it.getModObject()},
				mapOf("specId" to objectId)
			))
		} else {
			model.spec = ModelModification(null, null)
		}
		model.defaultActions = emptyList()

		invokeComposite(DataProviderMessages.message("security.server.audit.specification.progress.task"), queries, Consumer{consumer.accept(model)})
	}
}