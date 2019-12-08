package ru.coding4fun.intellij.database.data.property.agent.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.agent.OperatorDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
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
			queries.add(QueryDefinition(
				"sql/action/property/agent/operator/Alert.sql",
				DataProviderMessages.message("agent.operator.progress.alert"),
				Consumer { model.alerts = it.getObjects() },
				hashMapOf("operatorId" to objectId)
			))

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
}