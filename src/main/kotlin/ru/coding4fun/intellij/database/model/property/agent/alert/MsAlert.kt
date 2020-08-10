/*
 * Copyright [2020] Coding4fun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.coding4fun.intellij.database.model.property.agent.alert

import ru.coding4fun.intellij.database.model.common.Identity

data class MsAlert(
	override var id: String,
	override var name: String,
	val categoryName: String?,
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
			if (conditionParts.size == 5) {
				performanceObject = conditionParts[0]
				performanceCounter = conditionParts[1]
				performanceInstance = conditionParts[2]
				performanceSign = conditionParts[3]
				performanceValue = conditionParts[4].toLongOrNull()
			} else {
				performanceObject = conditionParts[0]
				performanceCounter = conditionParts[1]
				performanceInstance = null
				performanceSign = conditionParts[2]
				performanceValue = conditionParts[3].toLongOrNull()
			}
		} else {
			performanceObject = null
			performanceCounter = null
			performanceInstance = null
			performanceSign = null
			performanceValue = null
		}
	}
}