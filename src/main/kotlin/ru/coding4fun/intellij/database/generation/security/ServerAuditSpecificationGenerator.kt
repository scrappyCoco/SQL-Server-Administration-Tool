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
import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditSpecModel

object ServerAuditSpecificationGenerator : ScriptGeneratorBase<MsServerAuditSpecModel>() {
	override fun getCreatePart(
		model: MsServerAuditSpecModel,
		scriptBuilder: StringBuilder
	): StringBuilder {
		fill(model, scriptBuilder, true)
		return scriptBuilder
	}

	private fun fill(
		model: MsServerAuditSpecModel,
		scriptBuilder: StringBuilder,
		isCreateMode: Boolean
	) {
		if (isCreateMode) {
			scriptBuilder.append("CREATE ")
		} else {
			scriptBuilder.append("ALTER ")
		}

		val specification = model.spec.new ?: model.spec.old

		scriptBuilder.append("SERVER AUDIT SPECIFICATION ")
		scriptBuilder.append("[", specification.name, "]")
		scriptBuilder.appendJbLn()
		scriptBuilder.append("FOR SERVER AUDIT [", specification.auditName, "]")


		val mods = model.actions

		if (mods.any()) {
			scriptBuilder.addCommaWithNewLineScope()
				.also { separateScope ->
					for (actionModification in mods) {
						separateScope.invokeIf(actionModification.old.isSelected) {
							scriptBuilder.append(" DROP")
						}.invokeElse {
							scriptBuilder.append(" ADD")
						}

						scriptBuilder.append(" (", actionModification.new!!.name, ")")
					}
				}
		}

		scriptBuilder.append(" WITH (STATE = ")

		if (specification.isEnabled) {
			scriptBuilder.append("ON")
		} else {
			scriptBuilder.append("OFF")
		}

		scriptBuilder.append(")")
	}

	override fun getDropPart(
		model: MsServerAuditSpecModel,
		scriptBuilder: StringBuilder
	): StringBuilder {
		return scriptBuilder.append("DROP SERVER AUDIT SPECIFICATION [", model.spec.old.name, "]")
	}

	override fun getAlterPart(model: MsServerAuditSpecModel): String? {
		val scriptBuilder = StringBuilder()
		fill(model, scriptBuilder, false)
		return scriptBuilder.toString()
	}
}