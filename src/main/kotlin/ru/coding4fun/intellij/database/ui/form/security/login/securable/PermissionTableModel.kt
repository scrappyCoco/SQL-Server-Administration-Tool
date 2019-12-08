package ru.coding4fun.intellij.database.ui.form.security.login.securable

import ru.coding4fun.intellij.database.model.property.security.login.MsServerPermission
import ru.coding4fun.intellij.database.ui.form.common.MutableTableModel
import ru.coding4fun.intellij.database.ui.form.common.TableColumn
import ru.coding4fun.intellij.database.ui.form.common.TableColumnModel

class PermissionTableModel : MutableTableModel<MsServerPermission>(
	arrayListOf(),
	PermissionTableColumnModel
) {

	private object PermissionTableColumnModel : TableColumnModel<MsServerPermission> {
		private const val bitColumnWidth = 70

		override val columns: Array<TableColumn<MsServerPermission>>
			get() = arrayOf(permission, grantor, grant, withGrant, deny)

		private val permission = TableColumn<MsServerPermission>(
			"Permission",
			String::class.javaObjectType,
			get = { model -> model.name }
		)

		private val grantor = TableColumn<MsServerPermission>(
			"Grantor",
			String::class.javaObjectType,
			get = { model -> if (model.grantor != null) model.grantor!! else "" }
		)

		private val grant = TableColumn<MsServerPermission>(
			"Grant",
			Boolean::class.javaObjectType,
			get = { model -> model.grant },
			set = { model, aValue ->
				val boolValue = aValue as Boolean
				model.grant = boolValue

				if (boolValue) {
					model.withGrant = false
					model.deny = false
				}
			},
			size = bitColumnWidth
		)

		private val withGrant = TableColumn<MsServerPermission>(
			"With Grant",
			Boolean::class.javaObjectType,
			get = { model -> model.withGrant },
			set = { model, aValue ->
				val boolValue = aValue as Boolean
				model.withGrant = boolValue

				if (boolValue) {
					model.grant = false
					model.deny = false
				}
			},
			size = bitColumnWidth
		)

		private val deny = TableColumn<MsServerPermission>(
			"Deny",
			Boolean::class.javaObjectType,
			get = { model -> model.deny },
			set = { model, aValue ->
				val boolValue = aValue as Boolean
				model.deny = boolValue

				if (boolValue) {
					model.grant = false
					model.withGrant = false
				}
			},
			size = bitColumnWidth
		)
	}
}