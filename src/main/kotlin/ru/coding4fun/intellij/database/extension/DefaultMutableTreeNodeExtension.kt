package ru.coding4fun.intellij.database.extension

import ru.coding4fun.intellij.database.model.tree.TreeLabel
import javax.swing.tree.DefaultMutableTreeNode

val DefaultMutableTreeNode.treeLabel
	get() = this.userObject as TreeLabel

val DefaultMutableTreeNode.dfmChildren
	get() = this.children().asSequence().map { it as DefaultMutableTreeNode }.toList()