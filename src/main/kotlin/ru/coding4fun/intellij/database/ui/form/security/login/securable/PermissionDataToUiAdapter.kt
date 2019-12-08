package ru.coding4fun.intellij.database.ui.form.security.login.securable

import ru.coding4fun.intellij.database.model.common.BuiltinPermission
import ru.coding4fun.intellij.database.model.property.security.login.MsServerPermission

object PermissionDataToUiAdapter {
	fun convertToUi(
		securableId: String,
		majorId: String,
		majorName: String,
		builtinPermissions: List<BuiltinPermission>,
		serverPermissions: List<MsServerPermission>
	): List<MsServerPermission> {

		val mapOfPermissions = serverPermissions
			.groupBy { p -> p.name }
			.mapValues { p -> p.value.first() }

		return builtinPermissions
			.map { permission ->
				val serverPermission = mapOfPermissions[permission.name]

				MsServerPermission(
					"$securableId:${permission.name}",// TODO(Should be not crunch)
					permission.name,
					majorId,
					majorName,
					permission.id,
					securableId,
					serverPermission?.grantor,
					serverPermission?.grant ?: false,
					serverPermission?.withGrant ?: false,
					serverPermission?.deny ?: false
				)
			}
			.toList()
	}
}