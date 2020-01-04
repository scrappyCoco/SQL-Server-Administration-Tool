package ru.coding4fun.intellij.database.data.property.agent.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.agent.ScheduleDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.property.agent.schedule.MsScheduleModel
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.function.Consumer

class ScheduleDataProviderImpl(project: Project) : MsClient(project), ScheduleDataProvider {
	override fun getModel(objectId: String?, consumer: Consumer<MsScheduleModel>) {
		val composite = MsScheduleModel()
		val queries = arrayListOf<QueryDefinition>()

		if (objectId == null) {
			composite.schedule = ModelModification(null, null)
			queries.add(QueryDefinition(
				"sql/action/property/agent/schedule/DefaultJobs.sql",
				DataProviderMessages.message("agent.schedule.progress.job"),
				Consumer { composite.jobs = it.getObjects() }
			))
		} else {
			queries.add(
				QueryDefinition(
					"sql/action/property/agent/schedule/Jobs.sql",
					DataProviderMessages.message("agent.schedule.progress.job"),
					Consumer { composite.jobs = it.getObjects() },
					hashMapOf("scheduleId" to objectId)
				)
			)

			queries.add(
				QueryDefinition(
					"sql/action/property/agent/schedule/Schedule.sql",
					DataProviderMessages.message("agent.schedule.progress.schedule"),
					Consumer { composite.schedule = it.getModObject() },
					hashMapOf("scheduleId" to objectId)
				)
			)
		}

		invokeComposite(
			DataProviderMessages.message("agent.schedule.progress.task"),
			queries,
			Consumer { consumer.accept(composite) })
	}

	override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsScheduleModel>>,
        errorConsumer: Consumer<Exception>
    ) {
		val models: HashMap<String, MsScheduleModel> =
			objectIds?.associateTo(HashMap(), { it to MsScheduleModel() }) ?: HashMap()
		val queries = arrayListOf<QueryDefinition>()

//		if (objectId == null) {
//			composite.schedule = ModelModification(null, null)
//			queries.add(QueryDefinition(
//				"sql/action/property/agent/schedule/DefaultJobs.sql",
//				DataProviderMessages.message("agent.schedule.progress.job"),
//				Consumer { composite.jobs = it.getObjects() }
//			))
//		} else {
//			queries.add(
//				QueryDefinition(
//					"sql/action/property/agent/schedule/Jobs.sql",
//					DataProviderMessages.message("agent.schedule.progress.job"),
//					Consumer { composite.jobs = it.getObjects() },
//					hashMapOf("scheduleId" to objectId)
//				)
//			)
//
//			queries.add(
//				QueryDefinition(
//					"sql/action/property/agent/schedule/Schedule.sql",
//					DataProviderMessages.message("agent.schedule.progress.schedule"),
//					Consumer { composite.schedule = it.getModObject() },
//					hashMapOf("scheduleId" to objectId)
//				)
//			)
//		}

		invokeComposite(
			DataProviderMessages.message("agent.schedule.progress.task"),
			queries,
			Consumer { successConsumer.accept(models) },
			errorConsumer)
	}
}