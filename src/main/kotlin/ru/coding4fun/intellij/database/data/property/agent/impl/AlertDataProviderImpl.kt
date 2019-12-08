package ru.coding4fun.intellij.database.data.property.agent.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.agent.AlertDataProvider
import ru.coding4fun.intellij.database.data.property.agent.PerformanceCounterManager
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.property.agent.alert.MsAlertModel
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.function.Consumer

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
}