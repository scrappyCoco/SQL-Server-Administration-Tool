package ru.coding4fun.intellij.database.model.tree

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Kind

data class TreeLabel(
	override var id: String,
	override var name: String,
	override val kind: MsKind,
	var isEnabled: Boolean? = null,
	var isRunning: Boolean? = null,
	var isNotUsed: Boolean? = null,
	var groupName: String? = null,
	var children: ArrayList<TreeLabel>? = null
) : Identity, Copyable<TreeLabel>, Kind {
	override fun getCopy(): TreeLabel = copy()
	override fun toString(): String = name

	constructor(kind: MsKind, children: ArrayList<TreeLabel>? = arrayListOf()) : this(
		"",
		kind.name,
		kind,
		children = children
	)
}