package ru.coding4fun.intellij.database.tree.folder

import com.intellij.openapi.application.ApplicationManager
import ru.coding4fun.intellij.database.extension.treeLabel
import ru.coding4fun.intellij.database.model.tree.TreeLabel
import ru.coding4fun.intellij.database.tree.MsTreeModel
import javax.swing.tree.DefaultMutableTreeNode

abstract class FolderBuilder(val folderNode: DefaultMutableTreeNode, protected val treeModel: MsTreeModel) {
	init {
		this.clear()
	}

	private fun clear() {
		folderNode.removeAllChildren()
		treeModel.reload(folderNode)
		folderNode.treeLabel.children!!.clear()
	}

	abstract fun distribute(treeLabels: Collection<TreeLabel>)

	fun reDraw() {
		ApplicationManager.getApplication().invokeLater {
			treeModel.reload(folderNode)
		}
	}
}