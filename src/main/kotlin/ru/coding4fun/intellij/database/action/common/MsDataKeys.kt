package ru.coding4fun.intellij.database.action.common

import com.intellij.openapi.actionSystem.DataKey
import ru.coding4fun.intellij.database.model.tree.TreeLabel
import ru.coding4fun.intellij.database.tree.MsTreeModel
import javax.swing.tree.DefaultMutableTreeNode

class MsDataKeys {
	companion object {
		val LABELS: DataKey<Array<TreeLabel>> = DataKey.create("mssql.Labels")
		val NODES: DataKey<Array<DefaultMutableTreeNode>> = DataKey.create("mssql.Nodes")
		val TREE_MODEL: DataKey<MsTreeModel> = DataKey.create("mssql.TreeModel")
	}
}