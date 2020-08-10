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
import ru.coding4fun.intellij.database.data.property.security.ServerAuditSpecificationDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditSpecModel
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditSpecification
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditSpecificationAction
import ru.coding4fun.intellij.database.ui.form.common.toMod
import ru.coding4fun.intellij.database.ui.form.common.toModList
import java.util.function.Consumer

class ServerAuditSpecificationDataProviderImpl(project: Project) : MsClient(project),
    ServerAuditSpecificationDataProvider {
    override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsServerAuditSpecModel>>,
        errorConsumer: Consumer<Exception>
    ) {
        val models: HashMap<String, MsServerAuditSpecModel> =
            objectIds?.associateTo(HashMap(), { it to MsServerAuditSpecModel() }) ?: HashMap()
        var specifications: List<MsServerAuditSpecification> = emptyList()
        var defaultServerAudits: List<BasicIdentity> = emptyList()
        var actions: List<MsServerAuditSpecificationAction> = emptyList()

        val queries = arrayListOf(
            QueryDefinition(
                "sql/action/property/security/server-audit/ServerAudits.sql",
                DataProviderMessages.message("security.server.audit.specification.progress.audit"),
                Consumer { defaultServerAudits = it.getObjects() }),
            QueryDefinition(
                "sql/action/property/security/server-audit/ServerAuditSpecification.sql",
                DataProviderMessages.message("security.server.audit.specification.progress.main"),
                Consumer { specifications = it.getObjects() }
            ),
            QueryDefinition(
                "sql/action/property/security/server-audit/ServerAuditSpecificationActions.sql",
                DataProviderMessages.message("security.server.audit.specification.progress.action"),
                Consumer { actions = it.getObjects() }
            )
        )

        if (objectIds == null) models[DbUtils.defaultId] = MsServerAuditSpecModel()

        invokeComposite(
            DataProviderMessages.message("security.server.audit.specification.progress.task"),
            queries,
            Consumer {
                val specificationMap = specifications.associateBy { it.id }
                val actionMap = actions.groupBy { it.specificationId }

                for (model in models) {
                    val specId = model.key
                    model.value.spec = (specificationMap[specId] ?: error("Unable to find audit specification with id $specId")).toMod()
                    model.value.defaultActions = actionMap[specId] ?: emptyList()
                    model.value.defaultServerAudits = defaultServerAudits
                    model.value.actions = model.value.defaultActions.toModList()
                }
                successConsumer.accept(models)
            })
    }
}