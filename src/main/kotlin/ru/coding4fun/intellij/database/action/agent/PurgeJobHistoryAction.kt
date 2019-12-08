package ru.coding4fun.intellij.database.action.agent

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.coding4fun.intellij.database.action.common.MsDataKeys
import ru.coding4fun.intellij.database.action.common.ScriptActionUtil.openSqlDocument
import ru.coding4fun.intellij.database.action.common.ScriptActionUtil.updateVisibility
import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.model.tree.MsKind

class PurgeJobHistoryAction : AnAction() {
	override fun actionPerformed(e: AnActionEvent) {
		val labels = e.getData(MsDataKeys.LABELS)!!
		val scriptBuilder = StringBuilder()

		for (label in labels) {
			scriptBuilder.append("EXEC msdb.dbo.sp_purge_jobhistory").appendJbLn()
				.append("  @job_name = '", label.name, "',").appendJbLn()
				.append("  @oldest_date = NULL").appendJbLn()
		}

		openSqlDocument(scriptBuilder.toString(), e.project!!)
	}

	override fun update(e: AnActionEvent) = updateVisibility(e, setOf(MsKind.JOB))
}