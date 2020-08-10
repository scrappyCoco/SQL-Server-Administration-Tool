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

package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.DbUtils
import ru.coding4fun.intellij.database.data.property.security.ServerRoleDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.common.BuiltinPermission
import ru.coding4fun.intellij.database.model.property.security.MsServerRoleModel
import ru.coding4fun.intellij.database.model.property.security.login.MsSecurable
import ru.coding4fun.intellij.database.model.property.security.login.MsServerPermission
import ru.coding4fun.intellij.database.model.property.security.role.RoleMember
import ru.coding4fun.intellij.database.model.property.security.role.ServerRole
import ru.coding4fun.intellij.database.ui.form.common.toMod
import ru.coding4fun.intellij.database.ui.form.common.toModList
import java.util.function.Consumer

class ServerRoleDataProviderImpl(project: Project) : MsClient(project), ServerRoleDataProvider {
	override fun getModels(
		objectIds: Array<String>?,
		successConsumer: Consumer<Map<String, MsServerRoleModel>>,
		errorConsumer: Consumer<Exception>
	) {
		val models: HashMap<String, MsServerRoleModel> =
			objectIds?.associateTo(HashMap(), { it to MsServerRoleModel() }) ?: HashMap()

		var builtin: List<BuiltinPermission> = emptyList()
		var serverPermissions: List<MsServerPermission> = emptyList()
		var members: List<RoleMember> = emptyList()
		var memberships: List<RoleMember> = emptyList()
		var securables: List<MsSecurable> = emptyList()
		var roles: List<ServerRole> = emptyList()

		val queries = arrayListOf(
			QueryDefinition(
				"sql/security/detail/BuiltinPermissions.sql",
				DataProviderMessages.message("security.server.role.progress.builtin"),
				Consumer { builtin = it.getObjects() }),
			QueryDefinition(
				"sql/action/property/security/login/ServerPermissions.sql",
				DataProviderMessages.message("security.login.progress.server.permission"),
				Consumer { serverPermissions = it.getObjects() }
			), QueryDefinition(
				"sql/action/property/security/login/Securables2.sql",
				DataProviderMessages.message("security.server.role.progress.securable"),
				Consumer { securables = it.getObjects() }
			), QueryDefinition(
				"sql/action/property/security/role/Role.sql",
				DataProviderMessages.message("security.server.role.progress.main"),
				Consumer { roles = it.getObjects() }
			), QueryDefinition(
				"sql/action/property/security/role/Memberships.sql",
				DataProviderMessages.message("security.server.role.progress.membership"),
				Consumer { memberships = it.getObjects() }
			), QueryDefinition(
				"sql/action/property/security/role/Members.sql",
				DataProviderMessages.message("security.server.role.progress.member"),
				Consumer { members = it.getObjects() }
			)
		)

		if (objectIds == null) models[DbUtils.defaultId] = MsServerRoleModel()

		invokeComposite(
			DataProviderMessages.message("security.server.role.progress.task"),
			queries,
			Consumer {
				val roleMap = roles.associateBy { it.id }
				val membershipMap = memberships.groupBy { it.principalId }
				val memberMap = members.groupBy { it.principalId }
				val securableMap = securables.groupBy { it.principalId }

				for (modelEntry in models) {
					val model = modelEntry.value
					val roleId = modelEntry.key

					model.role = (roleMap[roleId] ?: error("Unable to find server role with id $roleId")).toMod()
					model.builtin = builtin
					model.serverPermissions = serverPermissions
					model.securables = securableMap[roleId]!!
					model.memberships = membershipMap[roleId]!!
					model.members = memberMap[roleId]!!
					model.memberModList = model.members.toModList()
					model.membershipModList = model.memberships.toModList()
					model.serverPermissionModList = model.serverPermissions.toModList()
				}
				successConsumer.accept(models)
			}, errorConsumer
		)
	}
}