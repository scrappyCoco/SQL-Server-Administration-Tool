package ru.coding4fun.intellij.database.action.agent

import ru.coding4fun.intellij.database.action.KindPaths
import ru.coding4fun.intellij.database.action.common.NewModelAction
import ru.coding4fun.intellij.database.data.property.agent.AgentDataProviders
import ru.coding4fun.intellij.database.model.property.agent.alert.MsAlertModel
import ru.coding4fun.intellij.database.ui.form.agent.AlertDialog

class NewAlertAction: NewModelAction<MsAlertModel, AlertDialog>(
	KindPaths.alert,
	AlertDialog(),
	AgentDataProviders::getAlert) {
}