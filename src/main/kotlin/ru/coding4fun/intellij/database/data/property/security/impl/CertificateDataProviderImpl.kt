package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.security.CertificateDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.property.security.MsCertificateModel
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.function.Consumer

class CertificateDataProviderImpl(project: Project) : MsClient(project), CertificateDataProvider {
	override fun getModel(objectId: String?, consumer: Consumer<MsCertificateModel>) {
		val model = MsCertificateModel()

		val queries = arrayListOf(QueryDefinition(
			"sql/common/Database.sql",
			DataProviderMessages.message("security.asymmetric.key.progress.database"),
			Consumer { model.databases = it.getObjects() }
		))

		if (objectId == null) {
			model.certificate = ModelModification(null, null)
		} else {
			val idParts = separateId(objectId)
			val db = idParts[0]
			val id = idParts[1]

			queries.add(
				QueryDefinition(
					"sql/action/property/security/Certificate.sql",
					DataProviderMessages.message("security.certificate.progress.main"),
					Consumer { model.certificate = it.getModObject() },
					hashMapOf("db" to db, "certId" to id)
				)
			)
		}

		invokeComposite(
			DataProviderMessages.message("security.certificate.progress.task"),
			queries,
			Consumer { consumer.accept(model) })
	}
}