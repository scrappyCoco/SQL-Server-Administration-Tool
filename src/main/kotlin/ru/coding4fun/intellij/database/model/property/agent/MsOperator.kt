package ru.coding4fun.intellij.database.model.property.agent


import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Enable
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Named

data class MsOperator(
	override var id: String,
	override var name: String,
	override var isEnabled: Boolean,
	var eMail: String,
	var categoryName: String? = null
) : Identity, Copyable<MsOperator>, Enable, Named {
	override fun getCopy(): MsOperator = copy()
}