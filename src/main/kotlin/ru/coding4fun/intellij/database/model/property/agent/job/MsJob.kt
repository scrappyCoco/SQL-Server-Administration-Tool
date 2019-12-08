package ru.coding4fun.intellij.database.model.property.agent.job

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Enable
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.model.property.agent.MsNotifyLevel

data class MsJob(
	override var id: String,
	override var name: String,
	override var isEnabled: Boolean,
	var description: String? = null,
	var startStepId: String? = null,
	var categoryId: String? = null,
	var categoryName: String? = null,
	var ownerName: String? = null,
	var dateCreated: String? = null,
	var lastModified: String? = null,
	var lastExecuted: String? = null,
	// Notifications.
	var eMailNotifyLevel: MsNotifyLevel?,
	var eventLogLevel: MsNotifyLevel?,
	var eMailOperatorId: String?,
	var eMailOperatorName: String?,
	var deleteLevel: MsNotifyLevel?
) : Identity, Copyable<MsJob>, Enable, Named {
	override fun getCopy(): MsJob = copy()
	override fun toString(): String = name
}