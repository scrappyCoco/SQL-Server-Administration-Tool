package ru.coding4fun.intellij.database.model.property.agent.alert

import ru.coding4fun.intellij.database.model.common.Identity

data class MsAlert(
	override var id: String,
	override var name: String,
	var isEnabled: Boolean,
	var type: AlertType,
	var databaseName: String?,
	var errorNumber: String?,
	var severity: String?,
	var messageText: String?,
	var wmiNamespace: String?,
	var wmiQuery: String?,
	var jobId: String?,
	var jobName: String?,
	var includeEmail: Boolean,
	var notificationMessage: String?,
	var minutes: Int?,
	var seconds: Int?,
	var performanceCondition: String?
) : Identity {
	override fun toString(): String = name

	val performanceObject: String?
	val performanceCounter: String?
	val performanceInstance: String?
	val performanceSign: String?
	val performanceValue: Long?

	init {
		if (!performanceCondition.isNullOrEmpty()) {
			val conditionParts = performanceCondition!!.split("|")
			performanceObject = conditionParts[0]
			performanceCounter = conditionParts[1]
			performanceInstance = conditionParts[2]
			performanceSign = conditionParts[3]
			performanceValue = conditionParts[4].toLongOrNull()
		} else {
			performanceObject = null
			performanceCounter = null
			performanceInstance = null
			performanceSign = null
			performanceValue = null
		}
	}
}