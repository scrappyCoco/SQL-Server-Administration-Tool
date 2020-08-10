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
import ru.coding4fun.intellij.database.data.property.security.SymmetricKeyDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.property.security.MsSymmetricKey
import ru.coding4fun.intellij.database.model.property.security.MsSymmetricKeyModel
import ru.coding4fun.intellij.database.ui.form.common.toMod
import java.util.function.Consumer

class SymmetricKeyDataProviderImpl(project: Project) : MsClient(project), SymmetricKeyDataProvider {
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
        ), QueryDefinition(
            "sql/action/property/security/SymmetricKey.sql",
            DataProviderMessages.message("security.symmetric.key.progress.main"),
            Consumer { symmetricKeys = it.getObjects() }
        ))

        if (objectIds == null) models[DbUtils.defaultId] = MsSymmetricKeyModel()

        invokeComposite(
            DataProviderMessages.message("security.symmetric.key.progress.task"),
            queries,
            Consumer {
                val symmetricKeyMap = symmetricKeys.associateBy { it.id }
                for (modelEntry in models) {
                    val keyId = modelEntry.key
                    modelEntry.value.databases = databases
                    modelEntry.value.key = (symmetricKeyMap[keyId] ?: error("Unable to find symmetric key with id $keyId")).toMod()
                }
                successConsumer.accept(models)
            }, errorConsumer
        )
    }
}