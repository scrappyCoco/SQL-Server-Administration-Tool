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
	itemList: List<Model>
) : ModificationTracker<Model>
		where Model : Copyable<Model>,
			  Model : Selection {
	private val mods: HashMap<String, ModelModification<Model>> = hashMapOf()
	override fun getModifications(): List<ModelModification<Model>> = mods.values.toList()

	init {
		val rootNode = CheckedTreeNode("")
		for (item in itemList) {
			rootNode.add(CheckedTreeNode(item).also { it.isChecked = item.isSelected })
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
		if (mods.remove(selectedItem.id) == null) {
			mods[selectedItem.id] =
				ModelModification(selectedItem, selectedItem.getCopy().also { it.isSelected = node.isChecked })
		}
	}
}