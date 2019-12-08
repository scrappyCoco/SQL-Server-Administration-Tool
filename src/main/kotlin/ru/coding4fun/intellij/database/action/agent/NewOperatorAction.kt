package ru.coding4fun.intellij.database.action.agent

import ru.coding4fun.intellij.database.action.KindPaths
import ru.coding4fun.intellij.database.action.common.NewModelAction
import ru.coding4fun.intellij.database.data.property.agent.AgentDataProviders
import ru.coding4fun.intellij.database.model.property.agent.operator.MsOperatorModel
import ru.coding4fun.intellij.database.ui.form.agent.OperatorDialog

class NewOperatorAction : NewModelAction<MsOperatorModel, OperatorDialog>(
	KindPaths.operator,
	OperatorDialog(),
	AgentDataProviders::getOperator
)