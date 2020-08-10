/*
 * Copyright [2020] Coding4fun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.coding4fun.intellij.database.model.property.security.login

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.BuiltinPermission
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.model.property.security.role.RoleMember
import ru.coding4fun.intellij.database.ui.form.common.ModList
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.toModList

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
	var memberModList: ModList<RoleMember> = emptyList<RoleMember>().toModList()
	var serverPermissionModList: ModList<MsServerPermission> = emptyList<MsServerPermission>().toModList()
	var dbModList: ModList<MsDatabaseOfLogin> = emptyList<MsDatabaseOfLogin>().toModList()
	var dbRoleModList: ModList<MsDatabaseRoleMembership> = emptyList<MsDatabaseRoleMembership>().toModList()
	//endregion
	override var name: String
		get() = (login.new ?: login.old)!!.name
		set(_) {}
}