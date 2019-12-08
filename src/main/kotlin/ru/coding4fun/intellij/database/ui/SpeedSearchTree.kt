package ru.coding4fun.intellij.database.ui

import com.intellij.ide.ui.search.SearchUtil
import com.intellij.ui.CheckboxTree
import com.intellij.ui.CheckedTreeNode
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.TreeSpeedSearch
import javax.swing.JTree
import javax.swing.tree.TreePath

class SpeedSearchTree(rootNode: CheckedTreeNode, getTextFun: ((Any) -> String)) :
	CheckboxTree(SpeedSearchCellRenderer(getTextFun), rootNode, SPEED_POLICY) {
	private companion object {
		val SPEED_POLICY = CheckPolicy(false, true, true, false)
	}

	private lateinit var speedSearch: TreeSpeedSearch

	override fun installSpeedSearch() {
		val toStringFun: (TreePath) -> String = { p -> p.lastPathComponent.toString() }
		speedSearch = TreeSpeedSearch(this, toStringFun, true).also {
			(cellRenderer as SpeedSearchCellRenderer).speedSearch = it
		}
	}

	private class SpeedSearchCellRenderer(val getTextFun: ((Any) -> String)) : CheckboxTree.CheckboxTreeCellRenderer() {
		var speedSearch: TreeSpeedSearch? = null

		override fun customizeRenderer(tree: JTree?, value: Any?, selected: Boolean, expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean) {
			if (speedSearch == null) return
			if (value == null) return
			val text = getTextFun(value)
			val attributes = SimpleTextAttributes.REGULAR_ATTRIBUTES
			SearchUtil.appendFragments(
				speedSearch!!.enteredPrefix,
				text,
				attributes.style,
				attributes.fgColor,
				attributes.bgColor,
				textRenderer
			)
		}
	}
}