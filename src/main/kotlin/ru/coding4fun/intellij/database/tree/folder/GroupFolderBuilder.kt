package ru.coding4fun.intellij.database.tree.folder

import ru.coding4fun.intellij.database.model.tree.MsKind
import ru.coding4fun.intellij.database.model.tree.TreeLabel
import ru.coding4fun.intellij.database.tree.MsTreeModel
import javax.swing.tree.DefaultMutableTreeNode

class GroupFolderBuilder(folderNode: DefaultMutableTreeNode, treeModel: MsTreeModel, private val groupKind: MsKind) :
	FolderBuilder(folderNode, treeModel) {

	private val categoryFolders = mutableMapOf<String, DefaultMutableTreeNode>()

	override fun distribute(treeLabels: Collection<TreeLabel>) {
		for (treeLabel in treeLabels) {
			val groupNode = categoryFolders.getOrPut(treeLabel.groupName!!) {
				val groupLabel = TreeLabel("", treeLabel.groupName!!, groupKind)
				val treeNode = DefaultMutableTreeNode(groupLabel)
				folderNode.add(treeNode)
				treeNode
			}
			groupNode.add(DefaultMutableTreeNode(treeLabel))
		}
	}
}