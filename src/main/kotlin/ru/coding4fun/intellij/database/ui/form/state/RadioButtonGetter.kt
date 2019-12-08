package ru.coding4fun.intellij.database.ui.form.state

import java.util.function.Function
import javax.swing.JComponent
import javax.swing.JRadioButton

object RadioButtonGetter : Function<JComponent, Boolean> {
	override fun apply(radioButton: JComponent): Boolean {
		return (radioButton as JRadioButton).isSelected && radioButton.isEnabled
	}
}