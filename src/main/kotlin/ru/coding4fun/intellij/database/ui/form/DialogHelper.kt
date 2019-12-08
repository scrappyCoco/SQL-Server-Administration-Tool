package ru.coding4fun.intellij.database.ui.form

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.ui.form.state.CheckBoxGetter
import ru.coding4fun.intellij.database.ui.form.state.ComboBoxGetter
import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

object DialogHelper {
	fun initDialog(
		dialog: JDialog,
		contentPanel: JPanel,
		okButton: JButton,
		cancelButton: JButton,
		onOk: (() -> Unit),
		onCancel: (() -> Unit)
	) {
		dialog.contentPane = contentPanel
		dialog.isModal = true
		dialog.rootPane.defaultButton = okButton
		dialog.defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE

		dialog.addWindowListener(object : WindowAdapter() {
			override fun windowClosing(e: WindowEvent?) {
				onCancel()
			}
		})

		okButton.addActionListener { onOk() }
		cancelButton.addActionListener { onCancel() }

		contentPanel.registerKeyboardAction(
			{ onCancel() },
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
		)
	}

	private fun <T> getSelected(comboBox: JComboBox<T>): T? {
		if (ComboBoxGetter.apply(comboBox)) {
			@Suppress("UNCHECKED_CAST")
			return comboBox.selectedItem as T
		}

		return null
	}

	fun getSelectedName(comboBox: JComboBox<BasicIdentity>): String? {
		return if (!comboBox.isEnabled) null else getSelected(comboBox)?.name
	}

	fun getString(textField: JTextField): String? {
		if (TextFieldGetter.apply(textField)) {
			return textField.text
		}

		return null
	}

	fun getBoolean(checkBox: JCheckBox): Boolean {
		return CheckBoxGetter.apply(checkBox)
	}
}