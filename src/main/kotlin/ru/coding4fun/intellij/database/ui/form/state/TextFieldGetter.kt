package ru.coding4fun.intellij.database.ui.form.state

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
}