package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Selection

data class MsServerAuditSpecificationAction(
	override var id: String,
	override var name: String,
	override var isSelected: Boolean
) : Selection, Copyable<MsServerAuditSpecificationAction> {
	override fun getCopy(): MsServerAuditSpecificationAction = copy()
	override fun toString(): String = name
}