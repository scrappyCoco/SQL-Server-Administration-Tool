package ru.coding4fun.intellij.database.model.property.agent.job

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Identity

data class MsJobStep(
	override var id: String,
	override var name: String,
	var number: Int,
	var type: String,
	var onSuccessAction: Short,
	var onFailureAction: Short,
	var command: String,
	var dbName: String?,
	var proxyName: String?,
	var retryAttempts: Int,
	var retryInterval: Int,
	var outputFile: String?,
	var appendFile: Boolean,
	var overrideFile: Boolean,
	var stepHistory: Boolean,
	var jobHistory: Boolean,
	var abortEvent: Boolean,
	var appendTable: Boolean,
	var overrideTable: Boolean
) : Identity, Copyable<MsJobStep> {
	override fun getCopy(): MsJobStep = copy()
	override fun toString(): String = name
}