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

import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.function.Function
import javax.swing.JComponent
import javax.swing.text.JTextComponent

object TextFieldGetter : Function<JComponent, Boolean> {
	override fun apply(textComponent: JComponent): Boolean {
		val jTextField = textComponent as JTextComponent
		return jTextField.isEnabled && jTextField.text.isNotBlank()
	}

	fun getText(textComponent: JTextComponent): String? {
		return if (apply(textComponent)) textComponent.text else null
	}

	fun getTextOrCompute(textComponent: JTextComponent, compute: () -> String): String {
		return if (apply(textComponent)) textComponent.text else compute()
	}

	fun getInt(textComponent: JTextComponent): Int? {
		if (!apply(textComponent)) return null
		return textComponent.text.toIntOrNull()
	}

	fun getLong(textComponent: JTextComponent): Long? {
		if (!apply(textComponent)) return null
		return textComponent.text.toLongOrNull()
	}

	fun getIntOrCompute(textComponent: JTextComponent, compute: () -> Int): Int {
		if (!apply(textComponent)) return compute()
		return textComponent.text.toIntOrNull() ?: compute()
	}

	fun setActionHandler(
		comboBox: JTextComponent,
		actionHandler: (String?) -> Unit) {
		comboBox.addKeyListener(object : KeyListener {
			override fun keyTyped(event: KeyEvent?) { }
			override fun keyPressed(event: KeyEvent?) { }
			override fun keyReleased(event: KeyEvent?) {
				val selectedText = getText(comboBox)
				actionHandler.invoke(selectedText)
			}
		})
	}
}