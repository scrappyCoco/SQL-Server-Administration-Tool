package ru.coding4fun.intellij.database.model.property.agent.operator

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Selection

data class MsOperatorAlert(
	override var id: String,
	override var name: String,
	var sendToMail: Boolean
) : Identity, Copyable<MsOperatorAlert>, Selection {
	override fun getCopy(): MsOperatorAlert = copy()
	override fun toString(): String = name
	override var isSelected: Boolean
		get() = sendToMail
		set(value) {
			sendToMail = value
		}
}