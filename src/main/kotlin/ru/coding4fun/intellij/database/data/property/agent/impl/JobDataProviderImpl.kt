package ru.coding4fun.intellij.database.data.property.agent.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.agent.JobDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.property.agent.job.MsJobModel
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.function.Consumer

class JobDataProviderImpl(project: Project) : MsClient(project), JobDataProvider {
	override fun getModel(objectId: String?, consumer: Consumer<MsJobModel>) {
		val model = MsJobModel()
		val queries = arrayListOf(QueryDefinition(
			"sql/action/property/agent/job/SubSystem.sql",
			DataProviderMessages.message("agent.job.progress.sub.system"),
			Consumer { model.subSystems = it.getObjects() }
		), QueryDefinition(
			"sql/tree/agent/Operator.sql",
			DataProviderMessages.message("agent.job.progress.operator"),
			Consumer { model.operaotrs = it.getObjects() }
		), QueryDefinition(
			"sql/action/property/agent/job/Alert.sql",
			DataProviderMessages.message("agent.job.progress.alert"),
			Consumer { model.alerts = it.getObjects() },
			hashMapOf("jobId" to if (objectId.isNullOrEmpty()) "NULL" else "'$objectId'")
		), QueryDefinition(
			"sql/action/property/agent/job/Schedule.sql",
			DataProviderMessages.message("agent.job.progress.schedule"),
			Consumer { model.schedules = it.getObjects() },
			hashMapOf("jobId" to if (objectId.isNullOrEmpty()) "NULL" else "'$objectId'")
		), QueryDefinition("sql/action/property/agent/job/Category.sql",
			DataProviderMessages.message("agent.job.progress.category"),
			Consumer { model.categories = it.getObjects() }
		), QueryDefinition(
			"sql/common/Database.sql",
			DataProviderMessages.message("agent.job.progress.database"),
			Consumer { model.databases = it.getObjects() }
		))

		if (objectId.isNullOrBlank()) {
			model.job = ModelModification(null, null)
			model.steps = emptyList()
		} else {
			queries.add(
				QueryDefinition(
					"sql/action/property/agent/job/Step.sql",
					DataProviderMessages.message("agent.job.progress.step"),
					Consumer { model.steps = it.getObjects() },
					hashMapOf("jobId" to objectId)
				)
			)
			queries.add(
				QueryDefinition(
					"sql/action/property/agent/job/Job.sql",
					DataProviderMessages.message("agent.job.progress.job"),
					Consumer { model.job = it.getModObject() },
					hashMapOf("jobId" to objectId)
				)
			)
		}
		invokeComposite(
			DataProviderMessages.message("agent.job.progress.task"),
			queries,
			Consumer { consumer.accept(model) })
	}
}