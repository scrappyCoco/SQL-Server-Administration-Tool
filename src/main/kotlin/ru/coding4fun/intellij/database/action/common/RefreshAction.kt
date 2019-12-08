package ru.coding4fun.intellij.database.action.common

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.coding4fun.intellij.database.tree.MsTreeManager

class RefreshAction : AnAction() {
	override fun actionPerformed(event: AnActionEvent) {
		val selectedTreeLabels = event.getData(MsDataKeys.LABELS)
		val treeModel = event.getData(MsDataKeys.TREE_MODEL)!!
		if (selectedTreeLabels == null) {
			MsTreeManager.getInstance(event.project!!).refreshAll(treeModel)
		} else {
			val nodes = event.getData(MsDataKeys.NODES)!!
			MsTreeManager.getInstance(event.project!!).refreshSingleFolder(nodes.first(), treeModel)
		}
	}
}