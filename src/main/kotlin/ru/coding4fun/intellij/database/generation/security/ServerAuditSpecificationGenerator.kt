package ru.coding4fun.intellij.database.generation.security

import ru.coding4fun.intellij.database.extension.addCommaWithNewLineScope
import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditSpecModel

object ServerAuditSpecificationGenerator : ScriptGeneratorBase<MsServerAuditSpecModel>() {
	override fun getCreatePart(
		model: MsServerAuditSpecModel,
		scriptBuilder: StringBuilder,
		reverse: Boolean
	): StringBuilder {
		if (reverse) model.spec.reverse()
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

		val specification = (if (isCreateMode) model.spec.new else model.spec.new)!!

		scriptBuilder.append("SERVER AUDIT SPECIFICATION ")
		scriptBuilder.append("[", specification.name, "]")
		scriptBuilder.appendJbLn()
		scriptBuilder.append("FOR SERVER AUDIT [", specification.auditName, "]")


		val modifications = model.actions
			.filter { it.isModified }
			.toList()

		if (modifications.any()) {
			scriptBuilder.addCommaWithNewLineScope()
				.also { separateScope ->
					for (actionModification in modifications) {
						separateScope.invokeIf(actionModification.old?.isSelected == true) {
							scriptBuilder.append("DROP ")
						}.invokeElse {
							scriptBuilder.append("ADD ")
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
		return scriptBuilder.append("DROP SERVER AUDIT SPECIFICATION [", model.spec.old!!.name, "]")
	}

	override fun getAlterPart(model: MsServerAuditSpecModel): String? {
		val scriptBuilder = StringBuilder()
		fill(model, scriptBuilder, false)
		return scriptBuilder.toString()
	}
}