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

package ru.coding4fun.intellij.database.generation.security


import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.security.MsCryptographicProviderModel

object CryptographicProviderGenerator :
	ScriptGeneratorBase<MsCryptographicProviderModel>() {
	override fun getAlterPart(
		model: MsCryptographicProviderModel): String? {
		val provider = model.provider.new!!
		return "ALTER CRYPTOGRAPHIC PROVIDER [" + provider.name + "] FROM FILE = '" + provider.filePath + "';"
	}

	override fun getCreatePart(
		model: MsCryptographicProviderModel,
		scriptBuilder: StringBuilder
	): StringBuilder {
		val provider = model.provider.new!!
		scriptBuilder.append("CREATE CRYPTOGRAPHIC PROVIDER [", provider.name, "] FROM FILE = '", provider.filePath, "';")
		return scriptBuilder
	}

	override fun getDropPart(
		model: MsCryptographicProviderModel,
		scriptBuilder: StringBuilder
	): StringBuilder {
		val name = model.provider.old.name
		scriptBuilder
			.append("ALTER CRYPTOGRAPHIC PROVIDER [", name, "] DISABLE;")
			.appendJbLn()
			.append("DROP CRYPTOGRAPHIC PROVIDER [", name, "];")

		return scriptBuilder
	}
}