package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.security.AsymmetricKeyDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.property.security.MsAsymmetricKeyModel
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.function.Consumer

class AsymmetricKeyDataProviderImpl(project: Project) : MsClient(project), AsymmetricKeyDataProvider {
	override fun getModel(objectId: String?, consumer: Consumer<MsAsymmetricKeyModel>) {
		val model = MsAsymmetricKeyModel()
		model.algorithms = algorithms
		model.creationDispositions = creationDispositions

		val queries = arrayListOf(QueryDefinition(
			"sql/common/Database.sql",
			DataProviderMessages.message("security.asymmetric.key.progress.database"),
			Consumer { model.databases = it.getObjects() }
		))

		if (objectId == null) {
			model.asymKey = ModelModification(null, null)
		} else {
			val idParts = separateId(objectId)
			val db = idParts[0]
			val id = idParts[1]

			queries.add(
				QueryDefinition(
					"sql/action/property/security/AsymmetricKey.sql",
					DataProviderMessages.message("security.asymmetric.key.progress.main"),
					Consumer { model.asymKey = it.getModObject() },
					hashMapOf("db" to db, "keyId" to id)
				)
			)
		}

		invokeComposite(
			DataProviderMessages.message("security.asymmetric.key.progress.task"),
			queries,
			Consumer { consumer.accept(model) })
	}

	private val creationDispositions = arrayListOf("CREATE_NEW", "OPEN_EXISTING")
		.map { BasicIdentity(it, it) }

	private val algorithms = arrayListOf(
		"RSA_4096",
		"RSA_3072",
		"RSA_2048",
		"RSA_1024",
		"RSA_512"
	).map { BasicIdentity(it, it) }
}