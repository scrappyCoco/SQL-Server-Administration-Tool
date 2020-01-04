package ru.coding4fun.intellij.database.model.property.security.login

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.tree.MsKind

data class MsSecurable(
	override var id: String,
	override var name: String,
	var kind: MsKind,
	var classDesc: String,
	var majorId: String,
	var isExists: Boolean,
	var principalId: String?
) : Identity, Copyable<MsSecurable> {
	override fun getCopy(): MsSecurable = copy()
}