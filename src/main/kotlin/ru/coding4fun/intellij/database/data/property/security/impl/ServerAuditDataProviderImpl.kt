package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.security.ServerAuditDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.property.security.MsServerAudit
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditModel
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.function.Consumer

class ServerAuditDataProviderImpl(project: Project) : MsClient(project), ServerAuditDataProvider {
	override fun getModel(objectId: String?, consumer: Consumer<MsServerAuditModel>) {
		val model = MsServerAuditModel()
		if (objectId == null) {
			model.audit = ModelModification(null, null)
			consumer.accept(model)
		} else {
			val queries = listOf(QueryDefinition(
				"sql/action/property/security/server-audit/ServerAudit.sql",
				DataProviderMessages.message("security.server.audit.progress.main"),
				Consumer {model.audit = it.getModObject()},
				hashMapOf("auditGuid" to objectId)
			));
			invokeComposite(DataProviderMessages.message("security.server.audit.progress.task"), queries, Consumer {consumer.accept(model)})
		}
	}

	override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsServerAuditModel>>,
        errorConsumer: Consumer<Exception>
    ) {
		val models: HashMap<String, MsServerAuditModel> = objectIds?.associateTo(HashMap(), { it to MsServerAuditModel() }) ?: HashMap()
		var audits: List<MsServerAudit> = emptyList()

		if (objectIds == null) {
			models[""] = MsServerAuditModel().also { it.audit = ModelModification(null, null) }
		} else {
			val queries = listOf(QueryDefinition(
				"sql/action/property/security/server-audit/ServerAudit.sql",
				DataProviderMessages.message("security.server.audit.progress.main"),
				Consumer {audits = it.getObjects()}
			))

			invokeComposite(
				DataProviderMessages.message("security.server.audit.progress.task"),
				queries,
				Consumer {
					val auditMap = audits.associateBy { it.id }
					for (modelEntry in models) {
						modelEntry.value.audit = ModelModification(auditMap[modelEntry.key], null)
					}
					successConsumer.accept(models)
				}, errorConsumer
			)
		}
	}
}