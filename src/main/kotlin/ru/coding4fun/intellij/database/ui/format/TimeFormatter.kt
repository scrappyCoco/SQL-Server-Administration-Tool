package ru.coding4fun.intellij.database.ui.format

import com.intellij.database.util.replicate
import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter
import javax.swing.JFormattedTextField
import javax.swing.text.MaskFormatter

object TimeFormatter: MaskFormatter("##:##:##") {
	init {
		overwriteMode = true
		placeholder = "13:59:59"
	}

	fun setTime(intTime: Int, textField: JFormattedTextField) {
		var timeString = intTime.toString()
		timeString = "0".replicate(6 - timeString.length) + timeString

		val formattedString = timeString.substring(0, 2) + ":" +
				timeString.substring(2, 4) + ":" +
				timeString.substring(4, 6)

		textField.text = formattedString
	}

	fun getInt(textField: JFormattedTextField): Int {
		var text = TextFieldGetter.getText(textField) ?: return 0
		text = text.replace(":", "")
		return text.toInt()
	}
}