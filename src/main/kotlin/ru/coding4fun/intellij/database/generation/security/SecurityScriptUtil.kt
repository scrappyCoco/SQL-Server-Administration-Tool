package ru.coding4fun.intellij.database.generation.security


import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.extension.appendLnIfAbsent
import ru.coding4fun.intellij.database.model.property.security.login.MsServerPermission
import ru.coding4fun.intellij.database.ui.form.common.Modifications

object SecurityScriptUtil {
	fun appendServerPermissions(
		scriptBuilder: StringBuilder,
		serverPermissionModifications: Modifications<MsServerPermission>?,
		memberName: String
	) {
		if (serverPermissionModifications == null) {
			return
		}

		val modificationsOfServerPermissions = serverPermissionModifications
			.filter { it.isModified }
			.toList()

		if (!modificationsOfServerPermissions.any()) {
			return
		}

		scriptBuilder.appendLnIfAbsent()
		scriptBuilder.append("-- region Server permissions of ", memberName)
		scriptBuilder.appendJbLn()

		for (serverPermissionModification in modificationsOfServerPermissions) {
			val oldBitMask = serverPermissionModification.old?.getBitMask() ?: -1
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