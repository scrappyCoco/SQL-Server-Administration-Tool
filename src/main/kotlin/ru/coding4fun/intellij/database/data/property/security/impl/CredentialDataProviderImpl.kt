package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.security.CredentialDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.property.security.MsCredentialModel
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.function.Consumer

class CredentialDataProviderImpl(project: Project) : MsClient(project), CredentialDataProvider {
	override fun getModel(objectId: String?, consumer: Consumer<MsCredentialModel>) {
		val model = MsCredentialModel()

		val queries = arrayListOf<QueryDefinition>()
		if (objectId != null) {
			queries.add(
				QueryDefinition(
					"sql/action/property/security/Credential.sql",
					DataProviderMessages.message("security.credential.progress.main"),
					Consumer { model.credential = it.getModObject() },
					hashMapOf("credentialId" to objectId)
				)
			)
		} else {
			model.credential = ModelModification(null, null)
		}

		queries.add(
			QueryDefinition(
				"sql/tree/security/CryptographicProvider.sql",
				DataProviderMessages.message("security.credential.progress.crypto"),
				Consumer { model.cryptographicProviders = it.getObjects() },
				emptyMap()
			)
		)
		invokeComposite(
			DataProviderMessages.message("security.credential.progress.task"),
			queries,
			Consumer { consumer.accept(model) })
	}
}