package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.security.CryptographicDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.property.security.MsCryptographicProviderModel
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.function.Consumer

class CryptographicDataProviderImpl(project: Project) : MsClient(project), CryptographicDataProvider {
	override fun getModel(objectId: String?, consumer: Consumer<MsCryptographicProviderModel>) {
		val model = MsCryptographicProviderModel()
		if (objectId == null) {
			model.provider = ModelModification(null, null)
			consumer.accept(model)
		} else {
			val queries = listOf(
				QueryDefinition(
					"sql/action/property/security/CryptographicProvider.sql",
					DataProviderMessages.message("security.crypto.progress.main"),
					Consumer { model.provider = it.getModObject() },
					hashMapOf("providerId" to objectId)
				)
			)
			invokeComposite(
				DataProviderMessages.message("security.crypto.progress.task"),
				queries,
				Consumer { consumer.accept(model) })
		}
	}
}