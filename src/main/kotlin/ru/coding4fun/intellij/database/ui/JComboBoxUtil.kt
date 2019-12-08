package ru.coding4fun.intellij.database.ui

import ru.coding4fun.intellij.database.model.common.Identity
import javax.swing.AbstractButton
import javax.swing.JComboBox

fun <T> JComboBox<T>.addAll(items: Collection<T>) {
	for (item in items) {
		this.addItem(item)
	}

	if (this.itemCount > 0) {
		this.selectedIndex = 0
	}
}

fun <T> JComboBox<T>.synchronizeByName(
	items: List<T>,
	selection: String?,
	checkBox: AbstractButton?
) where T : Identity {
	val enableIfPossible = selection != null
	synchronize(this, items, checkBox, enableIfPossible) { listItem -> selection == null || listItem.name == selection }
}

fun <T> JComboBox<T>.synchronizeById(
	items: List<T>,
	selection: String?,
	checkBox: AbstractButton? = null
) where T : Identity {
	val enableIfPossible = selection != null
	synchronize(this, items, checkBox, enableIfPossible) { listItem -> selection == null || listItem.id == selection }
}

fun JComboBox<String>.synchronizeByString(
	items: List<String>,
	selection: String?,
	checkBox: AbstractButton? = null
) {
	val enableIfPossible = selection != null
	return synchronize(
		this,
		items,
		checkBox,
		enableIfPossible
	) { listItem -> selection == null || listItem == selection }
}

fun <T> synchronize(
	comboBox: JComboBox<T>,
	sourceItems: List<T>,
	checkBox: AbstractButton? = null,
	isCurrent: ((listItem: T) -> Boolean)?
) {
	synchronize(comboBox, sourceItems, checkBox, true, isCurrent)
}

fun <T> synchronize(
	comboBox: JComboBox<T>,
	sourceItems: List<T>,
	checkBox: AbstractButton? = null,
	enableIfPossible: Boolean,
	isCurrent: ((listItem: T) -> Boolean)?
) {
	comboBox.removeAllItems()
	if (checkBox != null) {
		checkBox.isSelected = false
		comboBox.isEnabled = false
	}

	var foundSelected = false
	for ((position, item) in sourceItems.withIndex()) {
		comboBox.addItem(item)
		if (!foundSelected && isCurrent?.invoke(item) == true) {
			comboBox.selectedIndex = position
			if (enableIfPossible) {
				if (checkBox != null) checkBox.isSelected = true
				comboBox.isEnabled = true
			}
			foundSelected = true
		}
	}
}

fun <T> initialize(
	comboBox: JComboBox<T>,
	sourceItems: List<T>
) {
	return synchronize(comboBox, sourceItems, null, true, null)
}