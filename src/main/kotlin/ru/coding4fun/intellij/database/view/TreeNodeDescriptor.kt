package ru.coding4fun.intellij.database.view

import com.intellij.ide.projectView.PresentationData
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.SimpleTextAttributes.*
import ru.coding4fun.intellij.database.extension.addSeparatorScope
import ru.coding4fun.intellij.database.model.tree.MsKind
import ru.coding4fun.intellij.database.model.tree.TreeLabel
import ru.coding4fun.intellij.database.model.tree.isGroup
import ru.coding4fun.intellij.database.model.tree.isRoot


object TreeNodeDescriptor {
	private val UNDERLINE_ATTRIBUTES = SimpleTextAttributes(STYLE_WAVED, null)

	fun updatePresentationForLabel(presentation: PresentationData, node: TreeLabel, viewOptions: MsViewOptions) {
		presentation.clear()

		val name: String = when {
			node.isRoot -> node.name
			node.isGroup -> node.name
			node.kind.isFolder -> node.kind.label!!
			else -> node.name
		}
		val textAttribute: SimpleTextAttributes = when {
			node.isNotUsed ?: false -> UNDERLINE_ATTRIBUTES
			else -> REGULAR_ATTRIBUTES
		}
		presentation.addText(name, textAttribute)
		presentation.setIcon(Kind2Icon.getIcon(node))

		val grayText = getGrayText(node, viewOptions)
		if (grayText != null) {
			presentation.addText(""" $grayText""", GRAYED_SMALL_ATTRIBUTES)
		}

		presentation.tooltip = getTooltip(node)
	}

	private fun getTooltip(node: TreeLabel): String {
		val tooltipBuilder = StringBuilder()
		tooltipBuilder.addSeparatorScope(if (node.kind.label == null) "" else node.kind.label + " is ") {
			tooltipBuilder.append(
				", "
			)
		}
			.invokeIf(node.isEnabled != null) { tooltipBuilder.append(if (node.isEnabled!!) "enabled" else "disabled") }
			.invokeIf(node.isRunning != null) { tooltipBuilder.append(if (node.isRunning!!) "running" else "not running") }
			.invokeIf(node.isNotUsed != null && node.isNotUsed!!) { tooltipBuilder.append("not used") }

		return tooltipBuilder.toString()
	}

	private fun getGrayText(node: TreeLabel, viewOptions: MsViewOptions): String? {
		if (node.children?.any() == true) {
			return node.children!!.size.toString()
		}

		val groups = arrayOf(MsKind.ASYMMETRIC_KEY, MsKind.SYMMETRIC_KEY)
		if (groups.contains(node.kind) && !viewOptions.groupByDb) {
			return node.groupName
		}
		return null
	}
}
