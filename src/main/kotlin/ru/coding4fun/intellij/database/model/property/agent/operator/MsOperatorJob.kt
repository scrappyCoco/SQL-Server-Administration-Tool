package ru.coding4fun.intellij.database.model.property.agent.operator

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Selection
import ru.coding4fun.intellij.database.model.property.agent.MsNotifyLevel

data class MsOperatorJob(
	override var id: String,
	override var name: String,
	var mailNotifyLevel: MsNotifyLevel?,
	override var isSelected: Boolean
) : Identity, Copyable<MsOperatorJob>, Selection {
	override fun getCopy(): MsOperatorJob = copy()
}