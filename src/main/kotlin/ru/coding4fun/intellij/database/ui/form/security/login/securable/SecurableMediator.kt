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

package ru.coding4fun.intellij.database.ui.form.security.login.securable

import com.intellij.ide.ui.search.SearchUtil
import com.intellij.ui.*
import com.intellij.ui.treeStructure.treetable.ListTreeTableModelOnColumns
import com.intellij.ui.treeStructure.treetable.TreeColumnInfo
import com.intellij.ui.treeStructure.treetable.TreeTable
import com.intellij.util.castSafelyTo
import com.intellij.util.ui.ColumnInfo
import ru.coding4fun.intellij.database.model.common.BuiltinPermission
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.property.security.login.MsSecurable
import ru.coding4fun.intellij.database.model.property.security.login.MsServerPermission
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.ModificationTracker
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode

class SecurableMediator(
	scrollPane: JScrollPane,
	securableAdapter: SecurableAdapter
) : ModificationTracker<MsServerPermission> {
	override fun getModifications(): List<ModelModification<MsServerPermission>> {
		return permissionMods.values.filter { it.old.getBitMask() != it.new?.getBitMask() ?: 0 }
	}

	private val table: MyTreeTable
	private var treeTableSpeedSearch: TreeTableSpeedSearch

	init {
		val rootNode = DefaultMutableTreeNode()
		var curClassDescNode: DefaultMutableTreeNode? = null
		val builtInPermissions = securableAdapter.getBuiltInPermission().groupBy { it.id }
		val permissions = securableAdapter.getServerPermissions().groupBy { it.securableId }
		var classBuiltInPermissions: List<BuiltinPermission>? = null

		for (securable in securableAdapter.getSecurables()) {
			if (curClassDescNode?.userObject?.toString() != securable.classDesc) {
				curClassDescNode = DefaultMutableTreeNode(securable.classDesc).also { rootNode.add(it) }
				classBuiltInPermissions = builtInPermissions[securable.classDesc]
			}
			val securableNode = DefaultMutableTreeNode(securable).also { curClassDescNode.add(it) }
			val securablePermissions = permissions[securable.id]?.associateBy { it.id } ?: emptyMap()

			for (builtIn in classBuiltInPermissions!!) {
				val existsPermission = securablePermissions[securable.id + ":" + builtIn.name]
				val permission = Permission(builtIn.name, existsPermission)
				securableNode.add(DefaultMutableTreeNode(permission))
			}
		}

		val denyColumn = DenyColumn()
		val grantPlusColumn = GrantPlusColumn()
		val grantColumn = GrantColumn()

		val boolColumns = listOf(grantColumn, grantPlusColumn, denyColumn)

		val columns = arrayOf<ColumnInfo<*, *>>(
			NameColumn(),
			GrantorColumn(),
			grantColumn,
			grantPlusColumn,
			denyColumn
		)

		val listTreeTableModel = MyListTreeTableModelOnColumns(rootNode, columns)

		table = MyTreeTable(listTreeTableModel)
		table.setTreeCellRenderer(MyCellRenderer())
		table.setRootVisible(false)
		scrollPane.viewport.add(table)
		treeTableSpeedSearch = TreeTableSpeedSearch(table)

		for (boolColumn in boolColumns) {
			val column = table.getColumn(boolColumn.name)
			column.maxWidth = 70
			column.minWidth = 70
		}
	}

	private val permissionMods = hashMapOf<String, ModelModification<MsServerPermission>>()

	private fun handleMod(prev: MsServerPermission?, current: MsServerPermission) {
		val key = current.id + ":" + current.name
		val existsMod = permissionMods[key]
		permissionMods[current.id + ":" + current.name] = ModelModification((existsMod?.old ?: prev)!!, current)
	}

	private class MyListTreeTableModelOnColumns(rootNode: TreeNode, columns: Array<ColumnInfo<*, *>>) :
		ListTreeTableModelOnColumns(rootNode, columns) {
		override fun setValueAt(aValue: Any?, node: Any?, column: Int) {
			super.setValueAt(aValue, node, column)
			nodeChanged(node as TreeNode?)
		}
	}

	private class MyTreeTable(treeTableModel: ListTreeTableModelOnColumns) : TreeTable(treeTableModel) {
		private val columns = treeTableModel.columns

		override fun getCellRenderer(row: Int, column: Int): TableCellRenderer? {
			return getFromColumnDefinition(row, column) { colDef, node -> colDef.getRenderer(node) }
				?: super.getCellRenderer(row, column)
		}

		override fun getCellEditor(row: Int, column: Int): TableCellEditor {
			return getFromColumnDefinition(row, column) { colDef, node -> colDef.getEditor(node) }
				?: super.getCellEditor(row, column)
		}

		private fun <T> getFromColumnDefinition(
			row: Int,
			column: Int,
			getter: ((BooleanColumn, DefaultMutableTreeNode) -> T)
		): T? {
			val treePath = tree.getPathForRow(row) ?: return null
			val node = treePath.lastPathComponent as DefaultMutableTreeNode
			return if (column == 0 || column == 1) null else getter(columns[column] as BooleanColumn, node)
		}
	}

	private inner class MyCellRenderer internal constructor() : ColoredTreeCellRenderer() {
		override fun customizeCellRenderer(
			tree: JTree,
			value: Any?,
			selected: Boolean,
			expanded: Boolean,
			leaf: Boolean,
			row: Int,
			hasFocus: Boolean
		) {
			val text = when (val userObject = value.castSafelyTo<DefaultMutableTreeNode>()?.userObject) {
				is String -> userObject
				is Identity -> userObject.name
				is Permission -> userObject.name
				else -> "-"
			}
			val attributes = SimpleTextAttributes.REGULAR_ATTRIBUTES

			SearchUtil.appendFragments(
				treeTableSpeedSearch.enteredPrefix,
				text,
				attributes.style,
				attributes.fgColor,
				attributes.bgColor,
				this
			)
		}
	}

	private class Permission(val name: String, var permission: MsServerPermission?) {
		fun setSecurable(securable: MsSecurable) {
			if (permission == null) {
				permission = MsServerPermission(
					securable.id,
					name,
					securable.majorId,
					securable.name,
					securable.classDesc,
					securable.id,
					null,
					false,
					false,
					false,
					""
				)
			}
		}

		var grantor: String
			get() = permission?.grantor ?: ""
			set(_) = Unit

		var deny: Boolean
			get() = permission?.deny ?: false
			set(value) {
				val perm = permission!!
				perm.grant = false
				perm.withGrant = false
				perm.deny = value
			}

		var grantPlus: Boolean
			get() = permission?.withGrant ?: false
			set(value) {
				val perm = permission!!
				perm.grant = false
				perm.withGrant = value
				perm.deny = false
			}

		var grant: Boolean
			get() = permission?.grant ?: false
			set(value) {
				val perm = permission!!
				perm.grant = value
				perm.withGrant = false
				perm.deny = false
			}
	}

	private class NameColumn : TreeColumnInfo("Name")

	private class GrantorColumn : ColumnInfo<DefaultMutableTreeNode, String>("Grantor") {
		override fun valueOf(item: DefaultMutableTreeNode?): String? = (item?.userObject as? Permission)?.grantor
		override fun getColumnClass(): Class<*> = String::class.java
		override fun isCellEditable(item: DefaultMutableTreeNode?): Boolean = false
	}

	private inner class GrantColumn : BooleanColumn("Grant") {
		override fun getPermission(permission: Permission): Boolean = permission.grant
		override fun setPermission(permission: Permission, isChecked: Boolean) {
			permission.grant = isChecked
		}
	}

	private inner class GrantPlusColumn : BooleanColumn("Grant++") {
		override fun getPermission(permission: Permission): Boolean = permission.grantPlus
		override fun setPermission(permission: Permission, isChecked: Boolean) {
			permission.grantPlus = isChecked
		}
	}

	private inner class DenyColumn : BooleanColumn("Deny") {
		override fun getPermission(permission: Permission): Boolean = permission.deny
		override fun setPermission(permission: Permission, isChecked: Boolean) {
			permission.deny = isChecked
		}
	}

	private abstract inner class BooleanColumn(name: String) : ColumnInfo<DefaultMutableTreeNode, Boolean>(name) {
		override fun getColumnClass(): Class<*> = Boolean::class.java
		override fun isCellEditable(item: DefaultMutableTreeNode?): Boolean = true
		override fun getEditor(item: DefaultMutableTreeNode?): TableCellEditor? = BooleanTableCellEditor()
		override fun getRenderer(item: DefaultMutableTreeNode?): TableCellRenderer? = BooleanTableCellRenderer()
		override fun setValue(item: DefaultMutableTreeNode?, value: Boolean?) {
			val permission = item!!.userObject.castSafelyTo<Permission>()!!
			val msSecurable = item.parent.castSafelyTo<DefaultMutableTreeNode>()!!.userObject as MsSecurable
			permission.setSecurable(msSecurable)
			val prevState = permission.permission?.getCopy()
			setPermission(permission, value!!)
			val currentState = permission.permission!!.getCopy()
			handleMod(prevState, currentState)
		}

		override fun valueOf(item: DefaultMutableTreeNode?): Boolean? {
			val permission = item?.userObject as? Permission ?: return null
			return getPermission(permission)
		}

		abstract fun setPermission(permission: Permission, isChecked: Boolean)
		abstract fun getPermission(permission: Permission): Boolean
	}
}