package ru.coding4fun.intellij.database.model.property.agent.job

import ru.coding4fun.intellij.database.model.common.*
import ru.coding4fun.intellij.database.model.tree.MsKind

data class MsSchedule(
	override var id: String,
	override var name: String,
	override var isSelected: Boolean,
	override var isEnabled: Boolean
) : Identity, Copyable<MsSchedule>, Selection, Kind, Enable {
	override fun getCopy(): MsSchedule = copy()
	override val kind: MsKind = MsKind.SCHEDULE
	override fun toString(): String = name
}