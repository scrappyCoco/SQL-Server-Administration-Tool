/*
 * Copyright [2020] Coding4fun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.coding4fun.intellij.database.data.property.agent.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.DbNull
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
import kotlin.collections.set

class AlertDataProviderImpl(project: Project) : AlertDataProvider, MsClient(project) {
//    override fun getModel(objectId: String?, consumer: Consumer<MsAlertModel>) {
//        val model = MsAlertModel()
//
//        val queries = arrayListOf(
//            QueryDefinition(
//                "sql/action/property/agent/alert/Database.sql",
//                DataProviderMessages.message("agent.alert.progress.database"),
//                Consumer { model.databases = it.getObjects() }
//            ),
//            QueryDefinition(
//                "sql/action/property/agent/alert/Operator.sql",
//                DataProviderMessages.message("agent.alert.progress.operator"),
//                Consumer { model.operators = it.getObjects() },
//                hashMapOf("alertId" to (objectId ?: "NULL"))
//            ),
//            QueryDefinition(
//                "sql/action/property/agent/alert/Job.sql",
//                DataProviderMessages.message("agent.alert.progress.jobs"),
//                Consumer { model.jobs = it.getObjects() }
//            ),
//            QueryDefinition(
//                "sql/action/property/agent/alert/PerformanceCounter.sql",
//                DataProviderMessages.message("agent.alert.progress.perf"),
//                Consumer { model.perfCounterManager = PerformanceCounterManager(it.getObjects()) })
//        )
//
//        if (objectId != null) {
//            queries.add(QueryDefinition(
//                "sql/action/property/agent/alert/Alert.sql",
//                DataProviderMessages.message("agent.alert.progress.alert"),
//                Consumer { model.alert = it.getModObject() }
//            ))
//        } else {
//            model.alert = ModelModification(null, null)
//        }
//
//        invokeComposite(
//            DataProviderMessages.message("agent.alert.progress.task"),
//            queries,
//            Consumer { consumer.accept(model) })
//    }

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

        if (objectIds == null) models[DbNull.value] = MsAlertModel()

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

        invokeComposite(
            DataProviderMessages.message("agent.alert.progress.task"),
            queries,
            Consumer {
                val alertMap = alerts.associateBy { it.id }
                for (modelEntry in models) {
                    val model = modelEntry.value
                    val modelId = modelEntry.key

                    model.databases = databases
                    model.operators = operators
                    model.jobs = jobs
                    model.alert = ModelModification(alertMap[modelId], null)
                    model.perfCounterManager = perfCounterManager!!
                }
                successConsumer.accept(models)
            },
            errorConsumer
        )
    }
}