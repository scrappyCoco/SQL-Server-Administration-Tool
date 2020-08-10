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


import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.extension.appendLnIfAbsent
import ru.coding4fun.intellij.database.model.property.security.login.MsServerPermission
import ru.coding4fun.intellij.database.ui.form.common.ModList

object SecurityScriptUtil {
	fun appendServerPermissions(
		scriptBuilder: StringBuilder,
		serverPermissionModList: ModList<MsServerPermission>?,
		memberName: String
	) {
		if (serverPermissionModList == null) {
			return
		}

		val modificationsOfServerPermissions = serverPermissionModList
			.filter { it.isModified }
			.toList()

		if (!modificationsOfServerPermissions.any()) {
			return
		}

		scriptBuilder.appendLnIfAbsent()
		scriptBuilder.append("-- region Server permissions of ", memberName)
		scriptBuilder.appendJbLn()

		for (serverPermissionModification in modificationsOfServerPermissions) {
			val oldBitMask = serverPermissionModification.old.getBitMask()
			val newPermission = serverPermissionModification.new!!
			val newBitMask = newPermission.getBitMask()

			if (oldBitMask == newBitMask) {
				continue
			}

			val action = when {
				newBitMask == 0 && oldBitMask > 0 -> "REVOKE"
				newPermission.deny -> "DENY"
				else -> "GRANT"
			}
			val option = if (!newPermission.withGrant) "" else " WITH GRANT OPTION "

			val permittedObject =
				if (newPermission.classDesc == "SERVER") ""
				else " ON ${newPermission.classDesc}::[${newPermission.majorName}]"

			scriptBuilder.append(action, " ", newPermission.name, " ")
			scriptBuilder.append(permittedObject, " TO [", memberName, "]", option)
			scriptBuilder.appendJbLn()
		}

		scriptBuilder.append("-- endregion")
		scriptBuilder.appendJbLn()
	}
}