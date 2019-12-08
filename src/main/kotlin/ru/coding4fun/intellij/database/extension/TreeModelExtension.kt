package ru.coding4fun.intellij.database.extension

import java.util.*
import javax.swing.tree.TreeModel
import javax.swing.tree.TreeNode

fun <T : TreeNode> TreeModel.getLeafNodes(aTreeNode: T): LinkedList<T> {
	val leadNodes = LinkedList<T>()

	fun processNode(pNode: T) {
		val children = pNode.children()
		while (children.hasMoreElements()) {
			val child = children.nextElement()
			if (child.isLeaf) {
				leadNodes.add(child as T)
			} else {
				processNode(child as T)
			}
		}
	}
	processNode(aTreeNode)

	return leadNodes
}