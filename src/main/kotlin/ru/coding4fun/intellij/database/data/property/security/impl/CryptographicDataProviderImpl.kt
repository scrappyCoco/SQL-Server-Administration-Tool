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
import ru.coding4fun.intellij.database.data.property.security.CryptographicDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.property.security.MsCryptographicProvider
import ru.coding4fun.intellij.database.model.property.security.MsCryptographicProviderModel
import ru.coding4fun.intellij.database.ui.form.common.toMod
import java.util.function.Consumer

class CryptographicDataProviderImpl(project: Project) : MsClient(project), CryptographicDataProvider {
	override fun getModels(
		objectIds: Array<String>?,
		successConsumer: Consumer<Map<String, MsCryptographicProviderModel>>,
		errorConsumer: Consumer<Exception>
	) {
		val models: HashMap<String, MsCryptographicProviderModel> =
			objectIds?.associateTo(HashMap(), { it to MsCryptographicProviderModel() }) ?: HashMap()
		var providers: List<MsCryptographicProvider> = emptyList()

		if (objectIds == null) models[DbUtils.defaultId] = MsCryptographicProviderModel()

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
					val cryptoId = model.key
					model.value.provider = (providerMap[cryptoId]
						?: error("Unable to find cryptographic provider with id $cryptoId")).toMod()
				}
				successConsumer.accept(models)
			}, errorConsumer
		)

	}
}