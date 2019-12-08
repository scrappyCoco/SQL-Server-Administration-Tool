package ru.coding4fun.intellij.database.model.property.agent.alert

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Kind
import ru.coding4fun.intellij.database.model.common.Selection
import ru.coding4fun.intellij.database.model.tree.MsKind

data class Operator(
	override var id: String,
	override var name: String,
	override var isSelected: Boolean
) : Identity, Copyable<Operator>, Kind, Selection {
	override val kind: MsKind = MsKind.OPERATOR
	override fun getCopy(): Operator = copy()
	override fun toString(): String = name
}