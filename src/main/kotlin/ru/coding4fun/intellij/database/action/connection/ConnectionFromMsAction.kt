package ru.coding4fun.intellij.database.action.connection

import com.intellij.database.console.session.DatabaseSessionManager
import com.intellij.database.dataSource.LocalDataSource
import com.intellij.database.psi.DbDataSource
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiElement
import ru.coding4fun.intellij.database.MsView
import ru.coding4fun.intellij.database.client.MsConnectionManager
import ru.coding4fun.intellij.database.client.MsDepartment
import ru.coding4fun.intellij.database.tree.MsTreeManager

class ConnectionFromMsAction : AnAction() {
	override fun actionPerformed(event: AnActionEvent) {
		MsConnectionManager.client = DatabaseSessionManager
			.facade(
				event.project!!,
				getLocalDataSource(event)!!,
				null,
				department = MsDepartment
			).client()

		val treeModel = MsView.getInstance(event.project!!).treeModel
		MsTreeManager.getInstance(event.project!!).refreshAll(treeModel)
	}

	private fun getLocalDataSource(event: AnActionEvent): LocalDataSource? {
		val element = event.getData(CommonDataKeys.PSI_ELEMENT) as PsiElement
		val dataSource = element as? DbDataSource ?: return null
		MsConnectionManager.dbDataSource = dataSource
		return dataSource.delegate as? LocalDataSource ?: return null
	}

	override fun update(event: AnActionEvent) {
		event.presentation.isVisible = getLocalDataSource(event) != null
		super.update(event)
	}
}