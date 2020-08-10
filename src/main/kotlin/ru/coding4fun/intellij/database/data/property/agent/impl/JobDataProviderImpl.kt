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
import ru.coding4fun.intellij.database.data.property.DbUtils
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
		var proxies: List<BasicIdentity> = emptyList()

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
		), QueryDefinition(
			"sql/action/property/agent/job/Job.sql",
			DataProviderMessages.message("agent.job.progress.job"),
			Consumer { jobs = it.getObjects() }
		), QueryDefinition(
			"sql/action/property/agent/job/Proxy.sql",
			DataProviderMessages.message("agent.job.progress.proxy"),
			Consumer { proxies = it.getObjects() }
		))

		if (objectIds == null) {
			models[DbUtils.defaultId] = MsJobModel()
		} else {
			queries.add(
				QueryDefinition(
					"sql/action/property/agent/job/Step.sql",
					DataProviderMessages.message("agent.job.progress.step"),
					Consumer { steps = it.getObjects() }
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
				val stepMap = steps.groupBy { it.jobId }

				for (model in models) {
					val job = model.value
					val jobId = model.key

					job.subSystems = subSystems
					job.operators = operators
					job.categories = categories
					job.databases = databases
					job.proxies = proxies
					job.alerts = alertMap[jobId] ?: emptyList()
					job.schedules = scheduleMap[jobId] ?: emptyList()
					job.job = ModelModification(jobMap[jobId] ?: error("Unable to find job with id $jobId"), null)
					job.steps = stepMap[jobId] ?: emptyList()
				}
				successConsumer.accept(models)
			}, errorConsumer)
	}
}