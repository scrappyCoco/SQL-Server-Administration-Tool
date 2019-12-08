package ru.coding4fun.intellij.database.ui

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree
import ru.coding4fun.intellij.database.action.PropertyHandler
import ru.coding4fun.intellij.database.action.common.MsDataKeys
import ru.coding4fun.intellij.database.extension.treeLabel
import ru.coding4fun.intellij.database.tree.MsTreeModel
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.tree.DefaultMutableTreeNode

class MsTree(private val project: Project) : Tree(), DataProvider, DumbAware {
	init {
		addMouseListener(OnDoubleClickOpenPropertyDialog(project))
	}

	override fun getData(dataId: String): Any? {
		if (CommonDataKeys.PROJECT.`is`(dataId)) {
			return project
		}

		val selectedUiElements = this.getSelectedNodes(DefaultMutableTreeNode::class.java) { true }
		if (selectedUiElements.any()) {
			when {
				MsDataKeys.LABELS.`is`(dataId) -> return selectedUiElements.map { uiElement ->
					uiElement.treeLabel
				}.toTypedArray()
				MsDataKeys.NODES.`is`(dataId) -> return selectedUiElements
				MsDataKeys.TREE_MODEL.`is`(dataId) -> return model as MsTreeModel
			}
		}

		return null
	}

	private class OnDoubleClickOpenPropertyDialog(private val project: Project) : MouseAdapter() {
		override fun mouseClicked(e: MouseEvent?) {
			if (e?.clickCount ?: 0 < 2) return
			val tree = e?.source as Tree
			val selectedTreeNode = tree.lastSelectedPathComponent as? DefaultMutableTreeNode ?: return
			val selectedTreeLabel = selectedTreeNode.treeLabel
			PropertyHandler.openDialog(project, selectedTreeLabel)
		}
	}
}