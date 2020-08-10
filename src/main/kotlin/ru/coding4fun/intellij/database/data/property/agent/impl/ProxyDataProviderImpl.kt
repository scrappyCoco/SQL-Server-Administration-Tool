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

package ru.coding4fun.intellij.database.data.property.agent.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.DbUtils
import ru.coding4fun.intellij.database.data.property.agent.ProxyDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.property.agent.proxy.MsProxy
import ru.coding4fun.intellij.database.model.property.agent.proxy.MsProxyModel
import ru.coding4fun.intellij.database.ui.form.common.toMod
import java.util.function.Consumer

class ProxyDataProviderImpl(project: Project) : MsClient(project), ProxyDataProvider {
    override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsProxyModel>>,
        errorConsumer: Consumer<Exception>
    ) {
        var proxies: List<MsProxy> = emptyList()
        var credentials: List<BasicIdentity> = emptyList()

        val models: HashMap<String, MsProxyModel> =
            objectIds?.associateTo(HashMap(), { it to MsProxyModel() }) ?: HashMap()

        val queries = arrayListOf(
            QueryDefinition("sql/action/property/agent/proxy/Proxies.sql",
                DataProviderMessages.message("agent.proxy.progress.proxy"),
                Consumer { proxies = it.getObjects() }
            ),
            QueryDefinition("sql/action/property/agent/proxy/Credentials.sql",
                DataProviderMessages.message("agent.proxy.progress.proxy"),
                Consumer { credentials = it.getObjects() }
            )
        )
        if (objectIds == null) models[DbUtils.defaultId] = MsProxyModel()

        invokeComposite(
            DataProviderMessages.message("agent.proxy.progress.task"),
            queries,
            Consumer {
                val proxyMap = proxies.associateBy { it.id }

                for (modelEntry in models) {
                    val proxyId = modelEntry.key
                    val model = modelEntry.value
                    model.proxy = (proxyMap[proxyId] ?: error("Unable to find proxy with id $proxyId")).toMod()
                    model.credentials = credentials
                }
                successConsumer.accept(models)
            }, errorConsumer
        )
    }
}