package ru.coding4fun.intellij.database.model.property.security.role

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Kind
import ru.coding4fun.intellij.database.model.common.Selection
import ru.coding4fun.intellij.database.model.tree.MsKind

data class RoleMember(
	override var id: String,
	override var name: String,
	override var isSelected: Boolean,
	override val kind: MsKind,
	var principalId: String?
) : Identity, Selection, Copyable<RoleMember>, Kind {
	override fun getCopy(): RoleMember = copy()
	override fun toString(): String = name
}