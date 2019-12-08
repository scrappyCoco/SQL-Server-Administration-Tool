package ru.coding4fun.intellij.database.generation.agent

import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.agent.alert.MsAlertModel

object AlertGenerator: ScriptGeneratorBase<MsAlertModel>() {
	override fun getCreatePart(model: MsAlertModel, scriptBuilder: StringBuilder, reverse: Boolean): StringBuilder {
		if (reverse) model.alert.reverse()
		return scriptBuilder
	}

	override fun getDropPart(model: MsAlertModel, scriptBuilder: StringBuilder): StringBuilder {
		return scriptBuilder
	}

	override fun getAlterPart(model: MsAlertModel): String? {
		return ""
	}
}