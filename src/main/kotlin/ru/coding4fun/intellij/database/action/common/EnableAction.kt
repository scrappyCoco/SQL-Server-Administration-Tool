package ru.coding4fun.intellij.database.action.common

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.coding4fun.intellij.database.action.common.ScriptActionUtil.openScriptByResourceHandler
import ru.coding4fun.intellij.database.action.common.ScriptActionUtil.updateByResourceHandler
import ru.coding4fun.intellij.database.model.tree.MsKind
import ru.coding4fun.intellij.database.model.tree.TreeLabel

class EnableAction : AnAction() {
	private companion object {
		val handlers = hashMapOf(
			MsKind.JOB to ResourceActionHandler(
				"Enable",
				"sql/action/enable/agent/Job.sql",
				::updateEnabled,
				::getId
			),
			MsKind.CRYPTOGRAPHIC_PROVIDER to ResourceActionHandler(
				"Enable Provider",
				"sql/action/enable/security/CryptographicProvider.sql",
				::updateEnabled,
				::getId
			),
			MsKind.ALERT to ResourceActionHandler(
				"Enable",
				"sql/action/enable/agent/Alert.sql",
				::updateEnabled,
				::getId
			),
			MsKind.AUDIT to ResourceActionHandler(
				"Enable",
				"sql/action/enable/security/Audit.sql",
				::updateEnabled,
				::getName
			),
			MsKind.SERVER_AUDIT_SPECIFICATION to ResourceActionHandler(
				"Enable",
				"sql/action/enable/security/ServerAuditSpecification.sql",
				::updateEnabled,
				::getName
			)
		)

		private fun getName(treeLabel: TreeLabel): String = treeLabel.name
		private fun getId(treeLabel: TreeLabel): String = treeLabel.id

		private fun updateEnabled(treeLabel: TreeLabel, event: AnActionEvent) {
			val isEnabled = treeLabel.isEnabled
			if (isEnabled != null) event.presentation.isEnabled = !isEnabled
		}
	}

	override fun update(e: AnActionEvent) = updateByResourceHandler(e, handlers)
	override fun actionPerformed(e: AnActionEvent) = openScriptByResourceHandler(e, handlers)
}