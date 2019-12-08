package ru.coding4fun.intellij.database.action.common

import com.intellij.openapi.actionSystem.AnActionEvent
import ru.coding4fun.intellij.database.model.tree.TreeLabel

class ResourceActionHandler(
	val text: String,
	val resource: String,
	val updateAction: ((TreeLabel, AnActionEvent) -> Unit)? = null,
	val fieldToSubstitute: ((TreeLabel) -> String) = { treeLabel -> treeLabel.id }
)