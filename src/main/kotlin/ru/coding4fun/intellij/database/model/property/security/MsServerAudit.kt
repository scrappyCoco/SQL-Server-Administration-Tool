package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Enable
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Named

data class MsServerAudit(
	override var id: String,
	override var name: String,
	override var isEnabled: Boolean,
	var queueDelay: Int? = null,
	var onAuditLogFailure: MsServerAuditOnFailureKind,
	var auditDestination: MsServerAuditDestination,
	var filePath: String? = null,
	var maxSize: Long? = null,
	var maxSizeUnit: String? = null,
	var maxRolloverFiles: Int? = null,
	var maxFiles: Int? = null,
	var reserveDiskSpace: Boolean? = null
) : Identity, Copyable<MsServerAudit>, Enable, Named {
	override fun getCopy(): MsServerAudit = copy()
	override fun toString(): String = name
}