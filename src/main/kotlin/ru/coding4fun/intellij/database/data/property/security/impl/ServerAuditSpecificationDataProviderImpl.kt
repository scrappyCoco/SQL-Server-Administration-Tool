package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.DbNull
import ru.coding4fun.intellij.database.data.property.security.ServerAuditSpecificationDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditSpecModel
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditSpecification
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditSpecificationAction
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.toModificationList
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
            queries.add(
                QueryDefinition(
                    "sql/action/property/security/server-audit/ServerAuditSpecification.sql",
                    DataProviderMessages.message("security.server.audit.specification.progress.main"),
                    Consumer { model.spec = it.getModObject() },
                    mapOf("specId" to objectId)
                )
            )
        } else {
            model.spec = ModelModification(null, null)
        }
        model.defaultActions = emptyList()

        invokeComposite(
            DataProviderMessages.message("security.server.audit.specification.progress.task"),
            queries,
            Consumer { consumer.accept(model) })
    }

    override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsServerAuditSpecModel>>,
        errorConsumer: Consumer<Exception>
    ) {
        val models: HashMap<String, MsServerAuditSpecModel> =
            objectIds?.associateTo(HashMap(), { it to MsServerAuditSpecModel() }) ?: HashMap()
        var specifications: List<MsServerAuditSpecification> = emptyList()
        var defaultServerAudits: List<BasicIdentity> = emptyList()
        var actions: List<MsServerAuditSpecificationAction> = emptyList()

        val queries = arrayListOf(
            QueryDefinition(
                "sql/action/property/security/server-audit/ServerAudits.sql",
                DataProviderMessages.message("security.server.audit.specification.progress.audit"),
                Consumer { defaultServerAudits = it.getObjects() }),
            QueryDefinition(
                "sql/action/property/security/server-audit/ServerAuditSpecification.sql",
                DataProviderMessages.message("security.server.audit.specification.progress.main"),
                Consumer { specifications = it.getObjects() }
            ),
            QueryDefinition(
                "sql/action/property/security/server-audit/ServerAuditSpecificationActions.sql",
                DataProviderMessages.message("security.server.audit.specification.progress.action"),
                Consumer { actions = it.getObjects() }
            )
        )

        if (objectIds == null) {
			models[DbNull.value] = MsServerAuditSpecModel()
        }

        invokeComposite(
            DataProviderMessages.message("security.server.audit.specification.progress.task"),
            queries,
            Consumer {
                val specificationMap = specifications.associateBy { it.id }
                val actionMap = actions.groupBy { it.specificationId }

                for (model in models) {
                    model.value.spec = ModelModification(specificationMap[model.key], null)
                    model.value.defaultActions = actionMap[model.key] ?: emptyList()
                    model.value.defaultServerAudits = defaultServerAudits
                    model.value.actions = model.value.defaultActions.toModificationList()
                }
                successConsumer.accept(models)
            })
    }
}