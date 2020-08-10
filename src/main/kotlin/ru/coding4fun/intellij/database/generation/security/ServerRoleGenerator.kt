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

package ru.coding4fun.intellij.database.generation.security


import ru.coding4fun.intellij.database.extension.appendLnIfAbsent
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.security.MsServerRoleModel
import ru.coding4fun.intellij.database.model.property.security.role.RoleMember
import ru.coding4fun.intellij.database.ui.form.common.ModList

object ServerRoleGenerator : ScriptGeneratorBase<MsServerRoleModel>() {
	override fun getAlterPart(model: MsServerRoleModel): String? {
		val scriptBuilder = StringBuilder()
		val newServerRoleName = model.role.new!!.name
		val oldServerRoleName = model.role.old.name

		if (oldServerRoleName != newServerRoleName) {
			scriptBuilder.append("ALTER SERVER ROLE [", oldServerRoleName, "] WITH NAME = [", newServerRoleName, "]")
		}
		generateForParams(newServerRoleName, model.memberModList, model.membershipModList, scriptBuilder)
		SecurityScriptUtil.appendServerPermissions(
			scriptBuilder,
			model.serverPermissionModList,
			newServerRoleName
		)
		return scriptBuilder.toString()
	}

	override fun getDropPart(model: MsServerRoleModel, scriptBuilder: StringBuilder): StringBuilder {
		scriptBuilder.append("DROP SERVER ROLE [", model.role.old.name, "];")
		return scriptBuilder
	}

	override fun getCreatePart(
		model: MsServerRoleModel,
		scriptBuilder: StringBuilder
	): StringBuilder {
		val role = model.role.new ?: model.role.old
		val roleName = role.name
		scriptBuilder.append("CREATE SERVER ROLE [", roleName, "]")
		if (role.auth != null) {
			scriptBuilder.append(" AUTHORIZATION [", role.auth, "]")
		}
		scriptBuilder.append(";")
		generateForParams(roleName, model.memberModList, model.membershipModList, scriptBuilder)
		SecurityScriptUtil.appendServerPermissions(scriptBuilder, model.serverPermissionModList, roleName)
		return scriptBuilder
	}

	private fun generateForParams(
		roleName: String,
		memberModList: ModList<RoleMember>,
		membershipModList: ModList<RoleMember>,
		scriptBuilder: StringBuilder
	) {
		for (member in memberModList) {
			scriptBuilder.appendLnIfAbsent().append("ALTER SERVER ROLE [", roleName, "]")
			scriptBuilder.append(if (member.new!!.isSelected) " ADD" else " DROP").append(" MEMBER ")
			scriptBuilder.append("[", member.new!!.name, "]")
		}

		for (membership in membershipModList) {
			scriptBuilder.appendLnIfAbsent().append("ALTER SERVER ROLE [", membership.new!!.name, "]")
			scriptBuilder.append(if (membership.new!!.isSelected) " ADD" else " DROP").append(" MEMBER ")
			scriptBuilder.append("[", roleName, "]")
		}
	}
}