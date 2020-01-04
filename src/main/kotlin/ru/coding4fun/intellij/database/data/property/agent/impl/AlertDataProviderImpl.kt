package ru.coding4fun.intellij.database.data.property.agent.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.agent.AlertDataProvider
import ru.coding4fun.intellij.database.data.property.agent.PerformanceCounterManager
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.property.agent.alert.MsAlert
import ru.coding4fun.intellij.database.model.property.agent.alert.MsAlertModel
import ru.coding4fun.intellij.database.model.property.agent.alert.Operator
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.arrayListOf
import kotlin.collections.associateBy
import kotlin.collections.associateTo
import kotlin.collections.emptyList
import kotlin.collections.groupBy
import kotlin.collections.hashMapOf
import kotlin.collections.iterator
import kotlin.collections.listOf
import kotlin.collections.set

class AlertDataProviderImpl(project: Project) : AlertDataProvider, MsClient(project) {
    override fun getModel(objectId: String?, consumer: Consumer<MsAlertModel>) {
        val model = MsAlertModel()

        val queries = arrayListOf(
            QueryDefinition(
                "sql/action/property/agent/alert/Database.sql",
                DataProviderMessages.message("agent.alert.progress.database"),
                Consumer { model.databases = it.getObjects() }
            ),
            QueryDefinition(
                "sql/action/property/agent/alert/Operator.sql",
                DataProviderMessages.message("agent.alert.progress.operator"),
                Consumer { model.operators = it.getObjects() },
                hashMapOf("alertId" to (objectId ?: "NULL"))
            ),
            QueryDefinition(
                "sql/action/property/agent/alert/Job.sql",
                DataProviderMessages.message("agent.alert.progress.jobs"),
                Consumer { model.jobs = it.getObjects() }
            ),
            QueryDefinition(
                "sql/action/property/agent/alert/PerformanceCounter.sql",
                DataProviderMessages.message("agent.alert.progress.perf"),
                Consumer { model.perfCounterManager = PerformanceCounterManager(it.getObjects()) })
        )

        if (objectId != null) {
            queries.add(QueryDefinition(
                "sql/action/property/agent/alert/Alert.sql",
                DataProviderMessages.message("agent.alert.progress.alert"),
                Consumer { model.alert = it.getModObject() }
            ))
        } else {
            model.alert = ModelModification(null, null)
        }

        invokeComposite(
            DataProviderMessages.message("agent.alert.progress.task"),
            queries,
            Consumer { consumer.accept(model) })
    }

    override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsAlertModel>>,
        errorConsumer: Consumer<Exception>
    ) {
        val models: HashMap<String, MsAlertModel> =
            objectIds?.associateTo(HashMap(), { it to MsAlertModel() }) ?: HashMap()
        var databases: List<BasicIdentity> = emptyList()
        var operators: List<Operator> = emptyList()
        var jobs: List<BasicIdentity> = emptyList()
        var perfCounterManager: PerformanceCounterManager? = null
        var alerts: List<MsAlert> = arrayListOf()
        val queries: ArrayList<QueryDefinition> = arrayListOf()

        if (objectIds == null) {
            models[""] = MsAlertModel().also { it.alert = ModelModification(null, null) }
        } else {
            queries.addAll(listOf(
                QueryDefinition(
                    "sql/action/property/agent/alert/Database.sql",
                    DataProviderMessages.message("agent.alert.progress.database"),
                    Consumer { databases = it.getObjects() }
                ),
                QueryDefinition(
                    "sql/action/property/agent/alert/Operator.sql",
                    DataProviderMessages.message("agent.alert.progress.operator"),
                    Consumer { operators = it.getObjects() }
                ),
                QueryDefinition(
                    "sql/action/property/agent/alert/Job.sql",
                    DataProviderMessages.message("agent.alert.progress.jobs"),
                    Consumer { jobs = it.getObjects() }
                ),
                QueryDefinition(
                    "sql/action/property/agent/alert/PerformanceCounter.sql",
                    DataProviderMessages.message("agent.alert.progress.perf"),
                    Consumer { perfCounterManager = PerformanceCounterManager(it.getObjects()) }),
                QueryDefinition(
                    "sql/action/property/agent/alert/Alert.sql",
                    DataProviderMessages.message("agent.alert.progress.alert"),
                    Consumer { alerts = it.getObjects() }
                ))
            )
        }

        invokeComposite(
            DataProviderMessages.message("agent.alert.progress.task"),
            queries,
            Consumer {
                val operatorMap = operators.groupBy { it.id }
                val alertMap = alerts.associateBy { it.id }
                for (model in models) {
                    model.value.databases = databases
                    model.value.operators = operatorMap[model.key] ?: emptyList()
                    model.value.jobs = jobs
                    model.value.alert = ModelModification(alertMap[model.key], null)
                    model.value.perfCounterManager = perfCounterManager!!
                }
                successConsumer.accept(models)
            },
            errorConsumer
        )
    }
}