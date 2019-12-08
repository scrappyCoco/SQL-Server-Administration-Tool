package ru.coding4fun.intellij.database.ui.form.common

import com.intellij.ui.CheckboxTreeListener
import com.intellij.ui.CheckedTreeNode
import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Selection
import ru.coding4fun.intellij.database.ui.SpeedSearchTree
import javax.swing.JScrollPane
import javax.swing.SwingConstants

class CheckBoxListModTracker<Model>(
	scrollPane: JScrollPane,
	itemList: List<Model>,
	mods: HashMap<String, ModelModification<Model>>
) : ModificationTracker<Model>
		where Model : Copyable<Model>,
			  Model : Selection {
	private val modifications: HashMap<String, ModelModification<Model>> = mods
	override fun getModifications(): List<ModelModification<Model>> = modifications.values.toList()

	constructor(
		scrollPane: JScrollPane,
		itemList: List<Model>
	) : this(scrollPane, itemList, hashMapOf())

	init {
		val rootNode = CheckedTreeNode("")
		for (item in itemList) {
			val isSelected = mods[item.id]?.new?.isSelected
			rootNode.add(CheckedTreeNode(item).also { it.isChecked = isSelected ?: item.isSelected })
		}
		SpeedSearchTree(rootNode, this::getText).also {
			it.addCheckboxTreeListener(object : CheckboxTreeListener {
				override fun nodeStateChanged(node: CheckedTreeNode) = captureChange(node)
			})
			scrollPane.viewport.add(it, SwingConstants.CENTER)
		}
	}

	private fun getText(value: Any): String {
		if (value !is CheckedTreeNode) return ""
		val userObject = value.userObject
		if (userObject !is Identity) return ""
		return userObject.name
	}

	private fun captureChange(node: CheckedTreeNode) {
		@Suppress("UNCHECKED_CAST") val selectedItem = node.userObject as Model
		if (modifications.remove(selectedItem.id) == null) {
			modifications[selectedItem.id] =
				ModelModification(selectedItem, selectedItem.getCopy().also { it.isSelected = node.isChecked })
		}
	}
}