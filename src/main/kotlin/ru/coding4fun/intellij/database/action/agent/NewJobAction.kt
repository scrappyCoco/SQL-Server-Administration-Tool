package ru.coding4fun.intellij.database.action.agent

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.coding4fun.intellij.database.action.KindPaths
import ru.coding4fun.intellij.database.action.common.ScriptActionUtil.updateVisibility
import ru.coding4fun.intellij.database.data.property.agent.AgentDataProviders
import ru.coding4fun.intellij.database.ui.displayDialog
import ru.coding4fun.intellij.database.ui.form.agent.JobDialog

class NewJobAction : AnAction() {
	override fun actionPerformed(e: AnActionEvent) =
		displayDialog(JobDialog(), e.project!!, AgentDataProviders.getJob(e.project!!))

	override fun update(e: AnActionEvent) = updateVisibility(e, KindPaths.job)
}