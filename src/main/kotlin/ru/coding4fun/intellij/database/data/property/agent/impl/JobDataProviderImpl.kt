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
import ru.coding4fun.intellij.database.data.property.agent.JobDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.property.agent.job.*
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.function.Consumer

class JobDataProviderImpl(project: Project) : MsClient(project), JobDataProvider {
//	override fun getModel(objectId: String?, consumer: Consumer<MsJobModel>) {
//		val model = MsJobModel()
//		val queries = arrayListOf(QueryDefinition(
//			"sql/action/property/agent/job/SubSystem.sql",
//			DataProviderMessages.message("agent.job.progress.sub.system"),
//			Consumer { model.subSystems = it.getObjects() }
//		), QueryDefinition(
//			"sql/tree/agent/Operator.sql",
//			DataProviderMessages.message("agent.job.progress.operator"),
//			Consumer { model.operators = it.getObjects() }
//		), QueryDefinition(
//			"sql/action/property/agent/job/Alert.sql",
//			DataProviderMessages.message("agent.job.progress.alert"),
//			Consumer { model.alerts = it.getObjects() },
//			hashMapOf("jobId" to if (objectId.isNullOrEmpty()) "NULL" else "'$objectId'")
//		), QueryDefinition(
//			"sql/action/property/agent/job/Schedule.sql",
//			DataProviderMessages.message("agent.job.progress.schedule"),
//			Consumer { model.schedules = it.getObjects() },
//			hashMapOf("jobId" to if (objectId.isNullOrEmpty()) "NULL" else "'$objectId'")
//		), QueryDefinition("sql/action/property/agent/job/Category.sql",
//			DataProviderMessages.message("agent.job.progress.category"),
//			Consumer { model.categories = it.getObjects() }
//		), QueryDefinition(
//			"sql/common/Database.sql",
//			DataProviderMessages.message("agent.job.progress.database"),
//			Consumer { model.databases = it.getObjects() }
//		))
//
//		if (objectId.isNullOrBlank()) {
//			model.job = ModelModification(null, null)
//			model.steps = emptyList()
//		} else {
//			queries.add(
//				QueryDefinition(
//					"sql/action/property/agent/job/Step.sql",
//					DataProviderMessages.message("agent.job.progress.step"),
//					Consumer { model.steps = it.getObjects() },
//					hashMapOf("jobId" to objectId)
//				)
//			)
//			queries.add(
//				QueryDefinition(
//					"sql/action/property/agent/job/Job.sql",
//					DataProviderMessages.message("agent.job.progress.job"),
//					Consumer { model.job = it.getModObject() },
//					hashMapOf("jobId" to objectId)
//				)
//			)
//		}
//		invokeComposite(
//			DataProviderMessages.message("agent.job.progress.task"),
//			queries,
//			Consumer { consumer.accept(model) })
//	}

    override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsJobModel>>,
        errorConsumer: Consumer<Exception>
    ) {
        val models: HashMap<String, MsJobModel> = objectIds?.associateTo(HashMap(), { it to MsJobModel() }) ?: HashMap()
        var subSystems: List<BasicIdentity> = emptyList()
        var operators: List<BasicIdentity> = emptyList()
        var alerts: List<MsAlert> = emptyList()
		var schedules: List<MsSchedule> = emptyList()
		var categories: List<BasicIdentity> = emptyList()
		var databases: List<BasicIdentity> = emptyList()
		var steps: List<MsJobStep> = emptyList()
		var jobs: List<MsJob> = emptyList()

		val queries = arrayListOf(QueryDefinition(
			"sql/action/property/agent/job/SubSystem.sql",
			DataProviderMessages.message("agent.job.progress.sub.system"),
			Consumer { subSystems = it.getObjects() }
		), QueryDefinition(
			"sql/tree/agent/Operator.sql",
			DataProviderMessages.message("agent.job.progress.operator"),
			Consumer { operators = it.getObjects() }
		), QueryDefinition(
			"sql/action/property/agent/job/Alert.sql",
			DataProviderMessages.message("agent.job.progress.alert"),
			Consumer { alerts = it.getObjects() }
		), QueryDefinition(
			"sql/action/property/agent/job/Schedule.sql",
			DataProviderMessages.message("agent.job.progress.schedule"),
			Consumer { schedules = it.getObjects() }
		), QueryDefinition("sql/action/property/agent/job/Category.sql",
			DataProviderMessages.message("agent.job.progress.category"),
			Consumer { categories = it.getObjects() }
		), QueryDefinition(
			"sql/common/Database.sql",
			DataProviderMessages.message("agent.job.progress.database"),
			Consumer { databases = it.getObjects() }
		))

		if (objectIds == null) {
			models[""] = MsJobModel().also {
				it.job = ModelModification(null, null)
			}
		} else {
			queries.add(
				QueryDefinition(
					"sql/action/property/agent/job/Step.sql",
					DataProviderMessages.message("agent.job.progress.step"),
					Consumer { steps = it.getObjects() }
				)
			)
			queries.add(
				QueryDefinition(
					"sql/action/property/agent/job/Job.sql",
					DataProviderMessages.message("agent.job.progress.job"),
					Consumer { jobs = it.getObjects() }
				)
			)
		}
		invokeComposite(
			DataProviderMessages.message("agent.job.progress.task"),
			queries,
			Consumer {
				val alertMap = alerts.groupBy { it.jobId }
				val scheduleMap = schedules.groupBy { it.jobId }
				val jobMap = jobs.associateBy { it.id }
				val stepMap = steps.groupBy { it.id }

				for (model in models) {
					model.value.subSystems = subSystems
					model.value.operators = operators
					model.value.categories = categories
					model.value.databases = databases
					model.value.alerts = alertMap[model.key] ?: emptyList()
					model.value.schedules = scheduleMap[model.key] ?: emptyList()
					model.value.job = ModelModification(jobMap[model.key], null)
					model.value.steps = stepMap[model.key] ?: emptyList()
				}
				successConsumer.accept(models)
			}, errorConsumer)
	}
}