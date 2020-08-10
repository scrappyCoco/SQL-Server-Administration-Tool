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

import ru.coding4fun.intellij.database.extension.addCommaWithNewLineScope
import ru.coding4fun.intellij.database.extension.appendGo
import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.extension.appendLnIfAbsent
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.security.MsAsymmetricKeyModel

object AsymmetricKeyGenerator :
	ScriptGeneratorBase<MsAsymmetricKeyModel>() {
	override fun getCreatePart(
		model: MsAsymmetricKeyModel,
		scriptBuilder: StringBuilder
	): StringBuilder {
		val asymKey = model.asymKey.new ?: model.asymKey.old
		scriptBuilder.append("USE [", model.asymKey.new!!.db, "]").appendGo()
		scriptBuilder.append("CREATE ASYMMETRIC KEY [", asymKey.name, "]").appendJbLn()

		if (asymKey.authorization?.isNotBlank() == true) {
			scriptBuilder.append("  AUTHORIZATION [", asymKey.authorization, "]").appendJbLn()
		}

		when {
			asymKey.file?.isNotBlank() == true ->
				scriptBuilder.append("  FROM FILE = '", asymKey.file, "'").appendJbLn()
			asymKey.executableFile?.isNotBlank() == true ->
				scriptBuilder.append("  FROM EXECUTABLE FILE = '", asymKey.executableFile, "'").appendJbLn()
			asymKey.assembly?.isNotBlank() == true ->
				scriptBuilder.append("  FROM ASSEMBLY = [", asymKey.assembly, "]").appendJbLn()
			asymKey.provider?.isNotBlank() == true ->
				scriptBuilder.append("  FROM PROVIDER [", asymKey.provider, "]").appendJbLn()
		}

		val isAlgorithmSet = asymKey.algorithm?.isNotBlank() ?: false
		val isProviderKeySet = asymKey.providerKeyName?.isNotBlank() == true
		val isCreationDispositionSet = asymKey.creationDisposition?.isNotBlank() == true

		if (isAlgorithmSet || isProviderKeySet || isCreationDispositionSet) {
			scriptBuilder.append("  WITH").addCommaWithNewLineScope().also {
				it.invokeIf(isAlgorithmSet) {
					scriptBuilder.append("  ALGORITHM = ", asymKey.algorithm)
				}.invokeIf(isProviderKeySet) {
					scriptBuilder.append("  PROVIDER_KEY_NAME = '", asymKey.providerKeyName, "'")
				}.invokeIf(isCreationDispositionSet) {
					scriptBuilder.append("  CREATION_DISPOSITION = ", asymKey.creationDisposition)
				}
			}
		}

		if (asymKey.password?.isNotBlank() == true) {
			scriptBuilder.appendLnIfAbsent().append("  ENCRYPTION BY PASSWORD = '", asymKey.password, "'")
		}

		scriptBuilder.append(";")
		return scriptBuilder
	}

	override fun getDropPart(
		model: MsAsymmetricKeyModel,
		scriptBuilder: StringBuilder
	): StringBuilder {
		val asymKey = model.asymKey.old
		return scriptBuilder.appendLnIfAbsent()
			.append("USE [", asymKey.db, "]").appendGo()
			.append("DROP ASYMMETRIC KEY ", "[", asymKey.name, "];")
	}

	override fun getAlterPart(model: MsAsymmetricKeyModel): String? {
		return ""
	}
}