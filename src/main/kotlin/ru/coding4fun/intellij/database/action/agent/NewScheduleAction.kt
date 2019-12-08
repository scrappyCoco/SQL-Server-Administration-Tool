package ru.coding4fun.intellij.database.action.agent

import ru.coding4fun.intellij.database.action.KindPaths
import ru.coding4fun.intellij.database.action.common.NewModelAction
import ru.coding4fun.intellij.database.data.property.agent.AgentDataProviders
import ru.coding4fun.intellij.database.model.property.agent.schedule.MsScheduleModel
import ru.coding4fun.intellij.database.ui.form.agent.ScheduleDialog

class NewScheduleAction : NewModelAction<MsScheduleModel, ScheduleDialog>(
	KindPaths.schedule,
	ScheduleDialog(),
	AgentDataProviders::getSchedule
)