package ru.coding4fun.intellij.database.ui.form.security.login.database

import com.intellij.ui.CheckboxTreeListener
import com.intellij.ui.CheckedTreeNode
import ru.coding4fun.intellij.database.model.property.security.login.MsDatabaseOfLogin
import ru.coding4fun.intellij.database.model.property.security.login.MsDatabaseRoleMembership
import ru.coding4fun.intellij.database.model.property.security.login.MsLoginModel
import ru.coding4fun.intellij.database.ui.SpeedSearchTree
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.ModificationTracker
import javax.swing.JScrollPane

class DatabaseMediator(
	treePanel: JScrollPane,
	model: MsLoginModel
) {
	private class UserInDb(
		var isSelected: Boolean,
		val roles: List<MsDatabaseRoleMembership>,
		val userInDb: MsDatabaseOfLogin,
		val publicRoleNode: CheckedTreeNode,
		var selectedRolesCount: Int = 0
	) {
		val roleMods = hashMapOf<String, MsDatabaseRoleMembership>()

		init {
			selectedRolesCount = roles.count { it.isSelected }
		}

		fun inverseSelection() {
			isSelected = !isSelected
			publicRoleNode.isChecked = isSelected
			if (!isSelected) deselectUser()
		}

		fun toggleRole(role: MsDatabaseRoleMembership, isChecked: Boolean) {
			selectedRolesCount += if (isChecked) 1 else -1

			if (role.isSelected == isChecked) roleMods.remove(role.name)
			else roleMods[role.name] = role.copy(isSelected = isChecked)
		}

		fun deselectUser() {
			roles.asSequence().filter { it.isSelected }.forEach { it.isSelected = false }
			roleMods.clear()
			selectedRolesCount = 0
		}
	}

	private val users = hashMapOf<String, UserInDb>()

	init {
		val rolesOfDbs = model.dbRoles.groupBy { it.databaseName }
		val rootNode = CheckedTreeNode()

		for (db in model.loginDatabases) {
			val dbNode = CheckedTreeNode().also {
				it.userObject = db
				it.isChecked = db.isSelected
				rootNode.add(it)
			}
			val roles = rolesOfDbs[db.name]!!

			var publicNode: CheckedTreeNode? = null
			var selectedRolesCount = 0
			for (role in roles) {
				CheckedTreeNode().also {
					it.userObject = role
					it.isChecked = role.isSelected
					dbNode.add(it)
					if ("public".equals(role.name, true)) publicNode = it.also {
						it.isChecked = db.isSelected
						it.isEnabled = false
					}
					if (role.isSelected) ++selectedRolesCount
				}
			}

			val userInDb = UserInDb(db.isSelected, roles, db, publicNode!!, selectedRolesCount)
			users[db.name] = userInDb
		}


		val checkBoxTree = SpeedSearchTree(rootNode, this::getTextForNode)
		treePanel.viewport.add(checkBoxTree)

		checkBoxTree.addCheckboxTreeListener(
			object : CheckboxTreeListener {
				override fun nodeStateChanged(node: CheckedTreeNode) {
					when (val userObject = node.userObject) {
						is MsDatabaseOfLogin -> updateDbUser(userObject, node)
						is MsDatabaseRoleMembership -> updateRole(userObject, node)
					}
				}
			})
	}

	private fun getTextForNode(value: Any): String {
		if (value !is CheckedTreeNode) return "-"
		return when (val userObject = value.userObject) {
			is MsDatabaseOfLogin -> userObject.name
			is MsDatabaseRoleMembership -> userObject.name
			else -> "-"
		}
	}

	private fun updateDbUser(db: MsDatabaseOfLogin, node: CheckedTreeNode) {
		val user = users[db.name]!!
		if (node.isChecked != user.isSelected) user.inverseSelection()
	}

	private fun updateRole(role: MsDatabaseRoleMembership, node: CheckedTreeNode) {
		val user = users[role.databaseName]!!
		user.toggleRole(role, node.isChecked)
	}

	val dbModTracker = DatabaseModificationTracker()
	val dbRoleModTracker = DatabaseRoleModificationTracker()

	inner class DatabaseModificationTracker : ModificationTracker<MsDatabaseOfLogin> {
		override fun getModifications(): List<ModelModification<MsDatabaseOfLogin>> {
			return users.values.filter { it.isSelected != it.userInDb.isSelected }
				.map { ModelModification(it.userInDb, it.userInDb.copy(isSelected = it.isSelected)) }
		}
	}

	inner class DatabaseRoleModificationTracker : ModificationTracker<MsDatabaseRoleMembership> {
		override fun getModifications(): List<ModelModification<MsDatabaseRoleMembership>> {
			return users.values.filter { it.isSelected && it.roleMods.any() }
				.flatMap { user ->
					user.roleMods.values.map {
						ModelModification(
							it.copy(isSelected = !it.isSelected),
							it
						)
					}
				}
		}
	}
}