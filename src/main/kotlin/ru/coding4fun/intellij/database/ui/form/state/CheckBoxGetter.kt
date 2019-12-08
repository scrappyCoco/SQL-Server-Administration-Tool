package ru.coding4fun.intellij.database.ui.form.state

import java.util.function.Function
import javax.swing.JCheckBox
import javax.swing.JComponent

object CheckBoxGetter : Function<JComponent, Boolean> {
	override fun apply(t: JComponent): Boolean {
		val jCheckBox = t as JCheckBox
		return jCheckBox.isEnabled && jCheckBox.isSelected
	}
}