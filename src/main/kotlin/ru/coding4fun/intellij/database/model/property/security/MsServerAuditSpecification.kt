package ru.coding4fun.intellij.database.model.property.security


import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Enable
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Named

data class MsServerAuditSpecification(
	override var id: String,
	override var name: String,
	override var isEnabled: Boolean,
	val auditName: String?
) : Identity, Named, Copyable<MsServerAuditSpecification>, Enable {
	override fun getCopy(): MsServerAuditSpecification {
		return copy()
	}
}