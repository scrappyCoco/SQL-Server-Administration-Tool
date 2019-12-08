package ru.coding4fun.intellij.database.action.connection

import com.intellij.database.console.session.DatabaseSessionManager
import com.intellij.database.dataSource.LocalDataSource
import com.intellij.database.psi.DbDataSource
import com.intellij.database.psi.DbPsiFacade
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.intellij.ui.popup.list.ListPopupImpl
import ru.coding4fun.intellij.database.MsView
import ru.coding4fun.intellij.database.client.MsConnectionManager
import ru.coding4fun.intellij.database.client.MsDepartment
import ru.coding4fun.intellij.database.tree.MsTreeManager

class ConnectionFromDbAction :
	AnAction("Add Connection", "Choose connection created by DataGrip", AllIcons.General.Add) {
	override fun actionPerformed(event: AnActionEvent) {
		val dataSources = DbPsiFacade.getInstance(event.project!!).dataSources.filter { it.dbms.isMicrosoft }
		val icons = dataSources.map { AllIcons.Providers.SqlServer }.toList()
		val baseListPopupStep = BaseListPopupStep("Choose datasource", dataSources, icons)
		val popup = JBPopupFactory.getInstance().createListPopup(baseListPopupStep)
		baseListPopupStep.doFinalStep {
			val mainList = (popup as ListPopupImpl).list
			MsConnectionManager.dbDataSource = (mainList.selectedValue as DbDataSource)
			MsConnectionManager.client = DatabaseSessionManager
				.facade(
					event.project!!,
					MsConnectionManager.dbDataSource!!.delegate as LocalDataSource,
					null,
					department = MsDepartment
				).client()

			val treeModel = MsView.getInstance(event.project!!).treeModel
			MsTreeManager.getInstance(event.project!!).refreshAll(treeModel)
		}

		popup.showInBestPositionFor(event.dataContext)
	}
}