package ru.coding4fun.intellij.database.model.property.security.login

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Kind
import ru.coding4fun.intellij.database.model.common.Selection
import ru.coding4fun.intellij.database.model.tree.MsKind

data class MsDatabaseRoleMembership(
	override var id: String,
	override var name: String,
	override var isSelected: Boolean,
	var databaseName: String
) : Selection, Copyable<MsDatabaseRoleMembership>, Kind {
	override val kind: MsKind = MsKind.DATABASE_ROLE
	override fun getCopy(): MsDatabaseRoleMembership = copy()
	override fun toString(): String = name
}