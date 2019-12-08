package ru.coding4fun.intellij.database.ui.format

import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter
import javax.swing.JFormattedTextField
import javax.swing.text.MaskFormatter

object DateFormatter : MaskFormatter("####-##-##") {
	init {
		overwriteMode = true
		placeholder = "2019-12-31"
	}

	fun setDate(intDate: Int, textField: JFormattedTextField) {
		val dateString = intDate.toString()
		if (dateString.length != 8) return
		val formattedString = dateString.substring(0, 4) + "-" +
				dateString.substring(4, 6) + "-" +
				dateString.substring(6, 8)

		textField.text = formattedString
	}

	fun getInt(textField: JFormattedTextField): Int {
		var text = TextFieldGetter.getText(textField) ?: return 0
		text = text.replace("-", "")
		return text.toInt()
	}
}