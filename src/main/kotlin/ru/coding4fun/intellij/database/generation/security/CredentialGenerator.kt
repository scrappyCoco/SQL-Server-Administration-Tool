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

import ru.coding4fun.intellij.database.extension.addSeparatorScope

import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.security.MsCredentialModel

object CredentialGenerator : ScriptGeneratorBase<MsCredentialModel>() {
	override fun getCreatePart(
		model: MsCredentialModel,
		scriptBuilder: StringBuilder
	): StringBuilder {
		val credential = model.credential.new ?: model.credential.old
		scriptBuilder.append("CREATE CREDENTIAL [", credential.name, "]").appendJbLn()
			.addSeparatorScope("  WITH ") { scriptBuilder.appendJbLn().append("  ") }.also {
				it.invoke { scriptBuilder.append("IDENTITY = '", credential.identityName, "'") }
				val hasPassword = !credential.password.isNullOrBlank()
				if (hasPassword) scriptBuilder.append(",")
				it.invokeIf(hasPassword) {
					scriptBuilder.append("SECRET = '", credential.password, "'")
				}
				it.invokeIf(!credential.providerName.isNullOrBlank()) {
					scriptBuilder.append("FOR CRYPTOGRAPHIC PROVIDER [", credential.providerName, "]")
				}
			}
		scriptBuilder.append(";")
		return scriptBuilder
	}

	override fun getDropPart(model: MsCredentialModel, scriptBuilder: StringBuilder): StringBuilder {
		return scriptBuilder.append("DROP CREDENTIAL [", model.credential.old.name, "];")
	}

	override fun getAlterPart(model: MsCredentialModel): String? {
		val scriptBuilder = StringBuilder()

		val credential = model.credential.new!!
		scriptBuilder.append("ALTER CREDENTIAL [", credential.name, "]").appendJbLn()
			.append("  WITH IDENTITY = '", credential.identityName, "'")

		if (!credential.password.isNullOrBlank()) {
			scriptBuilder.append(",").appendJbLn()
				.append("  SECRET = '", credential.password, "'")
		}

		return scriptBuilder.append(";").toString()
	}
}