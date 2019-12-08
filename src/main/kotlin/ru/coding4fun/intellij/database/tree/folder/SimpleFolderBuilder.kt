package ru.coding4fun.intellij.database.tree.folder

import ru.coding4fun.intellij.database.extension.treeLabel
import ru.coding4fun.intellij.database.model.tree.TreeLabel
import ru.coding4fun.intellij.database.tree.MsTreeModel
import javax.swing.tree.DefaultMutableTreeNode

class SimpleFolderBuilder(folderNode: DefaultMutableTreeNode, treeModel: MsTreeModel) :
	FolderBuilder(folderNode, treeModel) {
	override fun distribute(treeLabels: Collection<TreeLabel>) {
		for (treeLabel in treeLabels) {
			this.folderNode.add(DefaultMutableTreeNode(treeLabel))
			folderNode.treeLabel.children!!.add(treeLabel)
		}
	}
}