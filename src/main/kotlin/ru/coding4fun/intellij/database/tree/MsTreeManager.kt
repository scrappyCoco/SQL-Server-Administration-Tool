package ru.coding4fun.intellij.database.tree

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import javax.swing.tree.DefaultMutableTreeNode

interface MsTreeManager {
	fun refreshSingleFolder(selectedNode: DefaultMutableTreeNode, treeModel: MsTreeModel)
	fun refreshAll(treeModel: MsTreeModel)

	companion object {
		fun getInstance(project: Project) = ServiceManager.getService(project, MsTreeManager::class.java)!!
	}
}