package ru.coding4fun.intellij.database.model.property.agent.schedule

import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Named

class MsSchedule(
	override var id: String,
	override var name: String,
	var enabled: Boolean,
	var freqType: Int,
	var freqInterval: Int,
	var freqSubDayType: Int,
	var freqSubDayInterval: Int,
	var freqRelativeInterval: Int,
	var freqRecurrenceFactor: Int,
	var activeStartDate: Int,
	var activeEndDate: Int,
	var activeStartTime: Int,
	var activeEndTime: Int,
	var ownerLoginName: String?
): Named, Identity