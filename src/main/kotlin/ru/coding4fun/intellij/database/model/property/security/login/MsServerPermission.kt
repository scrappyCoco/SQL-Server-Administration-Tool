package ru.coding4fun.intellij.database.model.property.security.login

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Selection

data class MsServerPermission(
	override var id: String,
	override var name: String,
	var majorId: String,
	var majorName: String?,
	var classDesc: String,
	var securableId: String,
	var grantor: String?,
	var grant: Boolean,
	var withGrant: Boolean,
	var deny: Boolean
) : Identity, Copyable<MsServerPermission>, Selection {
	override var isSelected
		get() = getBitMask() > 0
		set(_) = Unit

	override fun getCopy(): MsServerPermission = copy()

	fun getBitMask(): Int {
		return (if (grant) 1 else 0) +
				(if (withGrant) 2 else 0) +
				(if (deny) 4 else 0)
	}
}