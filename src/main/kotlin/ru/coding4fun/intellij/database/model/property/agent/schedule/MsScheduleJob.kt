package ru.coding4fun.intellij.database.model.property.agent.schedule

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Selection

data class MsScheduleJob(
	override var id: String,
	override var name: String,
	override var isSelected: Boolean
) : Identity, Selection, Copyable<MsScheduleJob> {
	override fun getCopy(): MsScheduleJob = copy()
	override fun toString(): String = name
}