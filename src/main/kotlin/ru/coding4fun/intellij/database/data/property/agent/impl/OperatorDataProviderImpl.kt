package ru.coding4fun.intellij.database.data.property.agent.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.agent.OperatorDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.property.agent.MsOperator
import ru.coding4fun.intellij.database.model.property.agent.operator.MsOperatorAlert
import ru.coding4fun.intellij.database.model.property.agent.operator.MsOperatorJob
import ru.coding4fun.intellij.database.model.property.agent.operator.MsOperatorModel
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.function.Consumer

class OperatorDataProviderImpl(project: Project) : MsClient(project), OperatorDataProvider {
    override fun getModel(objectId: String?, consumer: Consumer<MsOperatorModel>) {
        val model = MsOperatorModel()
        val queries = arrayListOf(QueryDefinition(
            "sql/action/property/agent/operator/Categories.sql",
            DataProviderMessages.message("agent.operator.progress.category"),
            Consumer { model.operatorCategories = it.getObjects() }
        ))

        if (objectId == null) {
            model.jobs = emptyList()
            model.operator = ModelModification(null, null)

            queries.add(
                QueryDefinition(
                    "sql/action/property/agent/operator/AllAlert.sql",
                    DataProviderMessages.message("agent.operator.progress.alert"),
                    Consumer { model.alerts = it.getObjects() }
                )
            )
        } else {
            queries.add(
                QueryDefinition(
                    "sql/action/property/agent/operator/Alert.sql",
                    DataProviderMessages.message("agent.operator.progress.alert"),
                    Consumer { model.alerts = it.getObjects() },
                    hashMapOf("operatorId" to objectId)
                )
            )

            queries.add(
                QueryDefinition(
                    "sql/action/property/agent/operator/Job.sql",
                    DataProviderMessages.message("agent.operator.progress.job"),
                    Consumer { model.jobs = it.getObjects() },
                    hashMapOf("operatorId" to objectId)
                )
            )

            queries.add(
                QueryDefinition(
                    "sql/action/property/agent/operator/Operator.sql",
                    DataProviderMessages.message("agent.operator.progress.operator"),
                    Consumer { model.operator = it.getModObject() },
                    hashMapOf("operatorId" to objectId)
                )
            )
        }

        invokeComposite(
            DataProviderMessages.message("agent.operator.progress.task"),
            queries,
            Consumer { consumer.accept(model) })
    }

    override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsOperatorModel>>,
        errorConsumer: Consumer<Exception>
    ) {
        val models: HashMap<String, MsOperatorModel> =
            objectIds?.associateTo(HashMap(), { it to MsOperatorModel() }) ?: HashMap()
        var operatorCategories: List<BasicIdentity> = emptyList()
        var operators: List<MsOperator> = emptyList()
		var jobs: List<MsOperatorJob> = emptyList()
		var alerts: List<MsOperatorAlert> = emptyList()

        val queries = arrayListOf(QueryDefinition(
            "sql/action/property/agent/operator/Categories.sql",
            DataProviderMessages.message("agent.operator.progress.category"),
            Consumer { operatorCategories = it.getObjects() }
        ))

        if (objectIds == null) {

        } else {

        }

//        if (objectId == null) {
//            model.jobs = emptyList()
//            model.operator = ModelModification(null, null)
//
//            queries.add(
//                QueryDefinition(
//                    "sql/action/property/agent/operator/AllAlert.sql",
//                    DataProviderMessages.message("agent.operator.progress.alert"),
//                    Consumer { alerts = it.getObjects() }
//                )
//            )
//        } else {
//            queries.add(
//                QueryDefinition(
//                    "sql/action/property/agent/operator/Alert.sql",
//                    DataProviderMessages.message("agent.operator.progress.alert"),
//                    Consumer { alerts = it.getObjects() }
//                )
//            )
//
//            queries.add(
//                QueryDefinition(
//                    "sql/action/property/agent/operator/Job.sql",
//                    DataProviderMessages.message("agent.operator.progress.job"),
//                    Consumer { jobs = it.getObjects() }
//                )
//            )
//
//            queries.add(
//                QueryDefinition(
//                    "sql/action/property/agent/operator/Operator.sql",
//                    DataProviderMessages.message("agent.operator.progress.operator"),
//                    Consumer { operators = it.getObjects() }
//                )
//            )
//        }

        invokeComposite(
            DataProviderMessages.message("agent.operator.progress.task"),
            queries,
            Consumer { successConsumer.accept(models) },
            errorConsumer
        )
    }
}