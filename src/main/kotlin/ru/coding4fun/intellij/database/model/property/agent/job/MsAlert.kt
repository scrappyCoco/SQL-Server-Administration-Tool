package ru.coding4fun.intellij.database.model.property.agent.job

import ru.coding4fun.intellij.database.model.common.*
import ru.coding4fun.intellij.database.model.tree.MsKind


data class MsAlert(
	override var id: String,
	override var name: String,
	override var isEnabled: Boolean,
	override var isSelected: Boolean,
	var jobId: String?
) : Identity, Copyable<MsAlert>, Enable, Selection, Kind {
	override val kind: MsKind = MsKind.ALERT
	override fun getCopy(): MsAlert = copy()
	override fun toString(): String = name
}