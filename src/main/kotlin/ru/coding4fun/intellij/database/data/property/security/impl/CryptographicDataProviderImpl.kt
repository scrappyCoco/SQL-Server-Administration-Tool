package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.security.CryptographicDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.property.security.MsCryptographicProvider
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

    override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsCryptographicProviderModel>>,
        errorConsumer: Consumer<Exception>
    ) {
        val models: HashMap<String, MsCryptographicProviderModel> =
            objectIds?.associateTo(HashMap(), { it to MsCryptographicProviderModel() }) ?: HashMap()
        var providers: List<MsCryptographicProvider> = emptyList()

        if (objectIds == null) {
            models[""] = MsCryptographicProviderModel().also { it.provider = ModelModification(null, null) }
            successConsumer.accept(models)
        } else {
            val queries = listOf(
                QueryDefinition(
                    "sql/action/property/security/CryptographicProvider.sql",
                    DataProviderMessages.message("security.crypto.progress.main"),
                    Consumer {
                        providers = it.getObjects()
                    }
                )
            )

            invokeComposite(
                DataProviderMessages.message("security.crypto.progress.task"),
                queries,
                Consumer {
                    val providerMap = providers.associateBy { it.id }
                    for (model in models) {
                        model.value.provider = ModelModification(providerMap[model.key], null)
                    }
                    successConsumer.accept(models)
                }, errorConsumer
            )
        }
    }
}