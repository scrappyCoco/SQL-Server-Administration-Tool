package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.security.SymmetricKeyDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.property.security.MsSymmetricKey
import ru.coding4fun.intellij.database.model.property.security.MsSymmetricKeyModel
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.function.Consumer

class SymmetricKeyDataProviderImpl(project: Project) : MsClient(project), SymmetricKeyDataProvider {
    override fun getModel(objectId: String?, consumer: Consumer<MsSymmetricKeyModel>) {
        val model = MsSymmetricKeyModel()

        val queries = arrayListOf(QueryDefinition(
            "sql/common/Database.sql",
            DataProviderMessages.message("security.symmetric.key.progress.database"),
            Consumer { model.databases = it.getObjects() }
        ))

        if (objectId == null) {
            model.key = ModelModification(null, null)
        } else {
            val idParts = separateId(objectId)
            val db = idParts[0]
            val id = idParts[1]

            queries.add(
                QueryDefinition(
                    "sql/action/property/security/SymmetricKey.sql",
                    DataProviderMessages.message("security.symmetric.key.progress.main"),
                    Consumer { model.key = it.getModObject() },
                    hashMapOf("db" to db, "keyId" to id)
                )
            )
        }

        invokeComposite(
            DataProviderMessages.message("security.symmetric.key.progress.task"),
            queries,
            Consumer { consumer.accept(model) })
    }

    override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsSymmetricKeyModel>>,
        errorConsumer: Consumer<Exception>
    ) {
        val models: HashMap<String, MsSymmetricKeyModel> =
            objectIds?.associateTo(HashMap(), { it to MsSymmetricKeyModel() }) ?: HashMap()
        var databases: List<BasicIdentity> = emptyList()
        var symmetricKeys: List<MsSymmetricKey> = emptyList()

        val queries = arrayListOf(QueryDefinition(
            "sql/common/Database.sql",
            DataProviderMessages.message("security.symmetric.key.progress.database"),
            Consumer { databases = it.getObjects() }
        ))

        if (objectIds == null) {
            models[""] = MsSymmetricKeyModel().also { it.key = ModelModification(null, null) }
        } else {
            queries.add(
                QueryDefinition(
                    "sql/action/property/security/SymmetricKey.sql",
                    DataProviderMessages.message("security.symmetric.key.progress.main"),
                    Consumer { symmetricKeys = it.getObjects() }
                )
            )
        }

        invokeComposite(
            DataProviderMessages.message("security.symmetric.key.progress.task"),
            queries,
            Consumer {
                val symmetricKeyMap = symmetricKeys.associateBy { it.id }
                for (modelEntry in models) {
                    modelEntry.value.databases = databases
                    modelEntry.value.key = ModelModification(symmetricKeyMap[modelEntry.key], null)
                }
                successConsumer.accept(models)
            }, errorConsumer
        )
    }
}