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

package ru.coding4fun.intellij.database.ui.form.agent.operator

import com.intellij.ui.ComboBoxTableCellRenderer
import com.intellij.util.ui.table.ComboBoxTableCellEditor
import ru.coding4fun.intellij.database.model.property.agent.MsNotifyLevel
import ru.coding4fun.intellij.database.model.property.agent.operator.MsOperatorJob
import ru.coding4fun.intellij.database.ui.form.common.MutableTableModel
import ru.coding4fun.intellij.database.ui.form.common.TableColumn
import ru.coding4fun.intellij.database.ui.form.common.TableColumnModel

class OperatorJobTableModel : MutableTableModel<MsOperatorJob>(emptyList(), ColumnModel) {
	private object ColumnModel : TableColumnModel<MsOperatorJob> {
		override val columns: Array<TableColumn<MsOperatorJob>>
			get() = arrayOf(name, email)

		private val name = TableColumn<MsOperatorJob>(
			"Name",
			String::class.javaObjectType,
			get = { model -> model.name }
		)

		private val email = TableColumn<MsOperatorJob>(
			"Send to mail",
			MsNotifyLevel::class.javaObjectType,
			get = { model -> model.mailNotifyLevel },
			set = { model, aValue -> model.mailNotifyLevel = aValue as MsNotifyLevel },
			cellRenderer = ComboBoxTableCellRenderer.INSTANCE,
			cellEditor = ComboBoxTableCellEditor.INSTANCE
		)
	}
}