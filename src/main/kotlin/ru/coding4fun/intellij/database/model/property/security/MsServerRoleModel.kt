package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.BuiltinPermission
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.model.property.security.login.MsSecurable
import ru.coding4fun.intellij.database.model.property.security.login.MsServerPermission
import ru.coding4fun.intellij.database.model.property.security.role.RoleMember
import ru.coding4fun.intellij.database.model.property.security.role.ServerRole
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.Modifications

class MsServerRoleModel : Named {
	lateinit var serverPermissions: List<MsServerPermission>
	lateinit var role: ModelModification<ServerRole>
	lateinit var members: List<RoleMember>
	lateinit var memberships: List<RoleMember>
	lateinit var securables: List<MsSecurable>
	lateinit var builtin: List<BuiltinPermission>

	lateinit var memberModifications: Modifications<RoleMember>
	lateinit var membershipModifications: Modifications<RoleMember>
	lateinit var serverPermissionModifications: Modifications<MsServerPermission>
	override var name: String
		get() = (role.new ?: role.old)!!.name
		set(_) {}
}