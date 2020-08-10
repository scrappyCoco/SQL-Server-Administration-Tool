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

package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.BuiltinPermission
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.model.property.security.login.MsSecurable
import ru.coding4fun.intellij.database.model.property.security.login.MsServerPermission
import ru.coding4fun.intellij.database.model.property.security.role.RoleMember
import ru.coding4fun.intellij.database.model.property.security.role.ServerRole
import ru.coding4fun.intellij.database.ui.form.common.ModList
import ru.coding4fun.intellij.database.ui.form.common.ModelModification

class MsServerRoleModel : Named {
	lateinit var serverPermissions: List<MsServerPermission>
	lateinit var role: ModelModification<ServerRole>
	lateinit var members: List<RoleMember>
	lateinit var memberships: List<RoleMember>
	lateinit var securables: List<MsSecurable>
	lateinit var builtin: List<BuiltinPermission>

	lateinit var memberModList: ModList<RoleMember>
	lateinit var membershipModList: ModList<RoleMember>
	lateinit var serverPermissionModList: ModList<MsServerPermission>
	override var name: String
		get() = (role.new ?: role.old)!!.name
		set(_) {}
}