package ru.coding4fun.intellij.database.model.property.security.login

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Selection

data class MsDatabaseOfLogin(
    override var id: String,
    override var name: String,
    override var isSelected: Boolean = false,
    var user: String? = null,
    var defaultSchema: String? = null,
    val principalId: String
) : Selection, Copyable<MsDatabaseOfLogin> {
	override fun getCopy(): MsDatabaseOfLogin = copy()
}