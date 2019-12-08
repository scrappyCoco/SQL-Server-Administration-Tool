package ru.coding4fun.intellij.database.action.common

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.coding4fun.intellij.database.action.common.ScriptActionUtil.openScriptByResourceHandler
import ru.coding4fun.intellij.database.action.common.ScriptActionUtil.updateByResourceHandler
import ru.coding4fun.intellij.database.model.tree.MsKind
import ru.coding4fun.intellij.database.model.tree.TreeLabel

class StopAction: AnAction() {
	private companion object {
		val handlers = hashMapOf(
			MsKind.JOB to ResourceActionHandler(
				"Stop",
				"sql/action/stop/agent/Job.sql",
				::updateEnabled
			)
		)

		private fun updateEnabled(treeLabel: TreeLabel, event: AnActionEvent) {
			val isRunning = treeLabel.isRunning
			if (isRunning != null) event.presentation.isEnabled = isRunning
		}
	}

	override fun update(e: AnActionEvent) = updateByResourceHandler(e, handlers)
	override fun actionPerformed(e: AnActionEvent) = openScriptByResourceHandler(e, handlers)
}