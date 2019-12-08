package ru.coding4fun.intellij.database.generation.security


import ru.coding4fun.intellij.database.extension.appendLnIfAbsent
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.security.MsServerRoleModel
import ru.coding4fun.intellij.database.model.property.security.role.RoleMember
import ru.coding4fun.intellij.database.ui.form.common.Modifications

object ServerRoleGenerator : ScriptGeneratorBase<MsServerRoleModel>() {
	override fun getAlterPart(model: MsServerRoleModel): String? {
		val scriptBuilder = StringBuilder()
		val newServerRoleName = model.role.new!!.name
		val oldServerRoleName = model.role.old!!.name

		if (oldServerRoleName != newServerRoleName) {
			scriptBuilder.append("ALTER SERVER ROLE [", oldServerRoleName, "] WITH NAME = [", newServerRoleName, "]")
		}
		generateForParams(newServerRoleName, model.memberModifications, model.membershipModifications, scriptBuilder)
		SecurityScriptUtil.appendServerPermissions(
			scriptBuilder,
			model.serverPermissionModifications,
			newServerRoleName
		)
		return scriptBuilder.toString()
	}

	override fun getDropPart(model: MsServerRoleModel, scriptBuilder: StringBuilder): StringBuilder {
		scriptBuilder.append("DROP SERVER ROLE [", model.role.old!!.name, "];")
		return scriptBuilder
	}

	override fun getCreatePart(
		model: MsServerRoleModel,
		scriptBuilder: StringBuilder,
		reverse: Boolean
	): StringBuilder {
		model.role.reverse()
		val newModel = model.role.new!!
		val roleName = newModel.name
		scriptBuilder.append("CREATE SERVER ROLE [", roleName, "]")
		if (newModel.auth != null) {
			scriptBuilder.append(" AUTHORIZATION [", newModel.auth, "]")
		}
		scriptBuilder.append(";")
		generateForParams(roleName, model.memberModifications, model.membershipModifications, scriptBuilder)
		SecurityScriptUtil.appendServerPermissions(scriptBuilder, model.serverPermissionModifications, roleName)
		return scriptBuilder
	}

	private fun generateForParams(
		roleName: String,
		memberModifications: Modifications<RoleMember>,
		membershipModifications: Modifications<RoleMember>,
		scriptBuilder: StringBuilder
	) {
		for (member in memberModifications) {
			scriptBuilder.appendLnIfAbsent().append("ALTER SERVER ROLE [", roleName, "]")
			scriptBuilder.append(if (member.new!!.isSelected) " ADD" else " DROP").append(" MEMBER ")
			scriptBuilder.append("[", member.new!!.name, "]")
		}

		for (membership in membershipModifications) {
			scriptBuilder.appendLnIfAbsent().append("ALTER SERVER ROLE [", membership.new!!.name, "]")
			scriptBuilder.append(if (membership.new!!.isSelected) " ADD" else " DROP").append(" MEMBER ")
			scriptBuilder.append("[", roleName, "]")
		}
	}
}