package ru.coding4fun.intellij.database.action.agent.tool

//import ru.coding4fun.intellij.database.action.common.updateVisibility
//import ru.coding4fun.intellij.database.ui.displayDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.coding4fun.intellij.database.action.KindPaths
import ru.coding4fun.intellij.database.action.common.ScriptActionUtil.updateVisibility
import ru.coding4fun.intellij.database.data.property.EmptyDataProvider
import ru.coding4fun.intellij.database.ui.displayDialog
import ru.coding4fun.intellij.database.ui.form.agent.tool.FindJobDialog

class FindJobAction : AnAction() {
	override fun actionPerformed(e: AnActionEvent) = displayDialog(FindJobDialog(), e.project!!, EmptyDataProvider.findJob, null)
	override fun update(e: AnActionEvent) = updateVisibility(e, KindPaths.job)
}