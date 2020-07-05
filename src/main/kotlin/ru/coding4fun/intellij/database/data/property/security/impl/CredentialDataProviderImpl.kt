/*
 * Copyright [2020] Coding4fun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.DbNull
import ru.coding4fun.intellij.database.data.property.security.CredentialDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.property.security.MsCredential
import ru.coding4fun.intellij.database.model.property.security.MsCredentialModel
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import java.util.function.Consumer

class CredentialDataProviderImpl(project: Project) : MsClient(project), CredentialDataProvider {
//    override fun getModel(objectId: String?, consumer: Consumer<MsCredentialModel>) {
//        val model = MsCredentialModel()
//
//        val queries = arrayListOf<QueryDefinition>()
//        if (objectId != null) {
//            queries.add(
//                QueryDefinition(
//                    "sql/action/property/security/Credential.sql",
//                    DataProviderMessages.message("security.credential.progress.main"),
//                    Consumer { model.credential = it.getModObject() },
//                    hashMapOf("credentialId" to objectId)
//                )
//            )
//        } else {
//            model.credential = ModelModification(null, null)
//        }
//
//        queries.add(
//            QueryDefinition(
//                "sql/tree/security/CryptographicProvider.sql",
//                DataProviderMessages.message("security.credential.progress.crypto"),
//                Consumer { model.cryptographicProviders = it.getObjects() },
//                emptyMap()
//            )
//        )
//        invokeComposite(
//            DataProviderMessages.message("security.credential.progress.task"),
//            queries,
//            Consumer { consumer.accept(model) })
//    }

    override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsCredentialModel>>,
        errorConsumer: Consumer<Exception>
    ) {
        val models: HashMap<String, MsCredentialModel> =
            objectIds?.associateTo(HashMap(), { it to MsCredentialModel() }) ?: HashMap()
        var credentials: List<MsCredential> = emptyList()
        var cryptographicProviders: List<BasicIdentity> = emptyList()

        val queries = arrayListOf(
            QueryDefinition(
                "sql/tree/security/CryptographicProvider.sql",
                DataProviderMessages.message("security.credential.progress.crypto"),
                Consumer { cryptographicProviders = it.getObjects() },
                emptyMap()
            )
        )

        if (objectIds == null) {
            models[DbNull.value] = MsCredentialModel()
        } else {
            queries.add(
                QueryDefinition(
                    "sql/action/property/security/Credential.sql",
                    DataProviderMessages.message("security.credential.progress.main"),
                    Consumer { credentials = it.getObjects() }
                )
            )
        }

        invokeComposite(
            DataProviderMessages.message("security.credential.progress.task"),
            queries,
            Consumer {
				val credentialMap = credentials.associateBy { it.id }
				for (model in models) {
					model.value.credential = ModelModification(credentialMap[model.key], null)
					model.value.cryptographicProviders = cryptographicProviders
				}

				successConsumer.accept(models)
			}, errorConsumer
        )
    }
}