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
import ru.coding4fun.intellij.database.data.property.security.ServerAuditDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.property.security.MsServerAudit
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditModel
import ru.coding4fun.intellij.database.ui.form.common.toMod
import java.util.function.Consumer

class ServerAuditDataProviderImpl(project: Project) : MsClient(project), ServerAuditDataProvider {
	override fun getModels(
		objectIds: Array<String>?,
		successConsumer: Consumer<Map<String, MsServerAuditModel>>,
		errorConsumer: Consumer<Exception>
	) {
		val models: HashMap<String, MsServerAuditModel> =
			objectIds?.associateTo(HashMap(), { it to MsServerAuditModel() }) ?: HashMap()
		var audits: List<MsServerAudit> = emptyList()

		if (objectIds == null) models[DbUtils.defaultId] = MsServerAuditModel()

		val queries = listOf(QueryDefinition(
			"sql/action/property/security/server-audit/ServerAudit.sql",
			DataProviderMessages.message("security.server.audit.progress.main"),
			Consumer { audits = it.getObjects() }
		))

		invokeComposite(
			DataProviderMessages.message("security.server.audit.progress.task"),
			queries,
			Consumer {
				val auditMap = audits.associateBy { it.id }
				for (modelEntry in models) {
					val auditId = modelEntry.key
					modelEntry.value.audit =
						(auditMap[auditId] ?: error("Unable to find server audit with id $auditId")).toMod()
				}
				successConsumer.accept(models)
			}, errorConsumer
		)
	}
}