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
import ru.coding4fun.intellij.database.data.property.DbUtils
import ru.coding4fun.intellij.database.data.property.security.AsymmetricKeyDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.property.security.MsAsymmetricKey
import ru.coding4fun.intellij.database.model.property.security.MsAsymmetricKeyModel
import ru.coding4fun.intellij.database.ui.form.common.toMod
import java.util.function.Consumer

class AsymmetricKeyDataProviderImpl(project: Project) : MsClient(project), AsymmetricKeyDataProvider {
    override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsAsymmetricKeyModel>>,
        errorConsumer: Consumer<Exception>
    ) {
        val models: HashMap<String, MsAsymmetricKeyModel> =
            objectIds?.associateTo(HashMap(), { it to MsAsymmetricKeyModel() }) ?: HashMap()
        var asymmetricKeys: List<MsAsymmetricKey> = emptyList()
        var databases: List<BasicIdentity> = emptyList()

        val queries = arrayListOf(QueryDefinition(
            "sql/common/Database.sql",
            DataProviderMessages.message("security.asymmetric.key.progress.database"),
            Consumer { databases = it.getObjects() }
        ), QueryDefinition(
            "sql/action/property/security/AsymmetricKey.sql",
            DataProviderMessages.message("security.asymmetric.key.progress.main"),
            Consumer { asymmetricKeys = it.getObjects() }
        ))

        if (objectIds == null) models[DbUtils.defaultId] = MsAsymmetricKeyModel()

        invokeComposite(
            DataProviderMessages.message("security.asymmetric.key.progress.task"),
            queries,
            Consumer {
                val asymmetricKeyMap = asymmetricKeys.associateBy { it.id }
                for (modelEntry in models) {
                    modelEntry.value.algorithms = algorithms
                    modelEntry.value.creationDispositions = creationDispositions
					modelEntry.value.databases = databases
                    val keyId = modelEntry.key
                    modelEntry.value.asymKey = (asymmetricKeyMap[keyId] ?: error("Unable to find asymmetric key with id $keyId")).toMod()
                }
                successConsumer.accept(models)
            },
            errorConsumer
        )
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