package ru.coding4fun.intellij.database.action.common

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.coding4fun.intellij.database.action.common.ScriptActionUtil.openScriptByResourceHandler
import ru.coding4fun.intellij.database.action.common.ScriptActionUtil.updateByResourceHandler
import ru.coding4fun.intellij.database.model.tree.MsKind

class ViewAction : AnAction() {
	private companion object {
		val handlers = hashMapOf(
			MsKind.AGENT_ERROR_LOG to ResourceActionHandler(
				"View Agent Log",
				"sql/action/view/agent/ErrorLog.sql"
			),
			MsKind.JOB to ResourceActionHandler(
				"View Job History",
				"sql/action/view/agent/Job.sql"
			),
			MsKind.AUDIT to ResourceActionHandler(
				"View Audit Logs",
				"sql/action/view/security/Audit.sql"
			)
		)
	}

	override fun update(e: AnActionEvent) = updateByResourceHandler(e, handlers)
	override fun actionPerformed(e: AnActionEvent) = openScriptByResourceHandler(e, handlers)
}