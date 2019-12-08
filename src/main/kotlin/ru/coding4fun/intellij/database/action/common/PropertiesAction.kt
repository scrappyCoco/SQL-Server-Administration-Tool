package ru.coding4fun.intellij.database.action.common

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.coding4fun.intellij.database.action.PropertyHandler
import ru.coding4fun.intellij.database.model.tree.*

class PropertiesAction : AnAction() {
	override fun actionPerformed(e: AnActionEvent) {
		val selectedLabel = e.getData(MsDataKeys.LABELS)!!.first()
		PropertyHandler.openDialog(e.project!!, selectedLabel)
	}

	override fun update(e: AnActionEvent) {
		val objects = e.getData(MsDataKeys.LABELS)!!

		var isVisible = false
		while (true) {
			val firstObject = (if (objects.size != 1) null else objects[0]) ?: break
			if (firstObject.kind.isFolder) break
			isVisible =
				// Security.
				firstObject.isLogin ||
						firstObject.isCertificate ||
						firstObject.isAsymmetricKey ||
						firstObject.isCredential ||
						firstObject.isSymmetricKey ||
						firstObject.isCryptographicProvider ||
						firstObject.isServerAuditSpecification ||
						firstObject.isServerAudit ||
						firstObject.isServerRole ||
						// Sql Server Agent.
						firstObject.isJob ||
						firstObject.isOperator ||
						firstObject.isSchedule ||
						firstObject.isAlert
			break
		}

		e.presentation.isVisible = isVisible
	}
}