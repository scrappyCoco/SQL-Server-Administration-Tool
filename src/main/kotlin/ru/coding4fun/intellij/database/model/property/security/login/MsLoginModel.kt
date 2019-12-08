package ru.coding4fun.intellij.database.model.property.security.login

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.BuiltinPermission
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.model.property.security.role.RoleMember
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.Modifications
import ru.coding4fun.intellij.database.ui.form.common.toModificationList

class MsLoginModel : Named {
	//region Login
	lateinit var login: ModelModification<MsLogin>
	lateinit var certificates: List<BasicIdentity>
	lateinit var asymmetricKeys: List<BasicIdentity>
	lateinit var credentials: List<BasicIdentity>
	lateinit var databases: List<BasicIdentity>
	lateinit var languages: List<BasicIdentity>
	//endregion
	//region Securables
	lateinit var builtInPermission: List<BuiltinPermission>
	lateinit var securables: List<MsSecurable>
	lateinit var serverPermissions: List<MsServerPermission>
	//endregion
	lateinit var serverRoles: List<RoleMember>
	//region Databases
	lateinit var loginDatabases: List<MsDatabaseOfLogin>
	lateinit var dbRoles: List<MsDatabaseRoleMembership>
	//endregion
	//region Modifications
	var memberModifications: Modifications<RoleMember> = emptyList<RoleMember>().toModificationList()
	var serverPermissionModifications: Modifications<MsServerPermission> = emptyList<MsServerPermission>().toModificationList()
	var dbModifications: Modifications<MsDatabaseOfLogin> = emptyList<MsDatabaseOfLogin>().toModificationList()
	var dbRoleModifications: Modifications<MsDatabaseRoleMembership> = emptyList<MsDatabaseRoleMembership>().toModificationList()
	//endregion
	override var name: String
		get() = (login.new ?: login.old)!!.name
		set(_) {}
}