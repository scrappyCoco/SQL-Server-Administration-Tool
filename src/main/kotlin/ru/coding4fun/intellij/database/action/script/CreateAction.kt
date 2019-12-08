package ru.coding4fun.intellij.database.action.script

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.coding4fun.intellij.database.action.common.MsDataKeys
import ru.coding4fun.intellij.database.action.common.ScriptActionUtil.openScriptInEditor
import ru.coding4fun.intellij.database.model.tree.isReadOnly

class CreateAction : AnAction() {
	override fun actionPerformed(e: AnActionEvent) {
		val objects = e.getData(MsDataKeys.LABELS)!!
		val project = e.project!!
		openScriptInEditor(project, objects, InvokeKind.CREATE)
	}

	override fun update(event: AnActionEvent) {
		val objects = event.getData(MsDataKeys.LABELS)!!
		event.presentation.isVisible = objects.all { !it.isReadOnly }
	}
}