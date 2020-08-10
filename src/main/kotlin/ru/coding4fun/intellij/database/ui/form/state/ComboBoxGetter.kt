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

package ru.coding4fun.intellij.database.ui.form.state

import java.util.function.Function
import javax.swing.JComboBox
import javax.swing.JComponent

object ComboBoxGetter : Function<JComponent, Boolean> {
	override fun apply(t: JComponent): Boolean {
		val comboBox = (t as JComboBox<*>)
		return comboBox.isEnabled
	}

	fun getText(comboBox: JComboBox<*>): String? {
		return if (!apply(comboBox) || comboBox.selectedItem == null) null else comboBox.selectedItem!!.toString()
	}

	@Suppress("UNCHECKED_CAST")
	fun <Source, Result> getSelected(comboBox: JComboBox<Source>, map: (Source) -> Result): Result? {
		return if (!apply(comboBox) || comboBox.selectedItem == null) null else map(comboBox.selectedItem as Source)
	}

	fun <Source, Result> setActionHandler(
		comboBox: JComboBox<Source>,
		map: (Source) -> Result,
		actionHandler: (Result?) -> Unit) {
		comboBox.addActionListener {
			val selectedValue = getSelected(comboBox, map)
			actionHandler.invoke(selectedValue)
		}
	}
}