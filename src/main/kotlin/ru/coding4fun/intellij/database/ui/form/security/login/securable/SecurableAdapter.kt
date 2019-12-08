package ru.coding4fun.intellij.database.ui.form.security.login.securable

import ru.coding4fun.intellij.database.model.common.BuiltinPermission
import ru.coding4fun.intellij.database.model.property.security.login.MsSecurable
import ru.coding4fun.intellij.database.model.property.security.login.MsServerPermission

interface SecurableAdapter {
	fun getBuiltInPermission(): List<BuiltinPermission>
	fun getServerPermissions(): List<MsServerPermission>
	fun getSecurables(): List<MsSecurable>
	fun isAlterMode(): Boolean
}