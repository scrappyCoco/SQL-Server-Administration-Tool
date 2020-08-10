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

package ru.coding4fun.intellij.database.generation.agent

import ru.coding4fun.intellij.database.data.property.DbUtils
import ru.coding4fun.intellij.database.extension.addCommaWithNewLineScope
import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.agent.alert.MsAlertModel

object AlertGenerator: ScriptGeneratorBase<MsAlertModel>() {
	override fun getCreatePart(model: MsAlertModel, scriptBuilder: StringBuilder): StringBuilder {
		val newName = DbUtils.getNewName(model.alert) ?: "null"
		val newAlert = model.alert.new!!
		val delay = (newAlert.minutes ?: 0) * 60 + (newAlert.seconds ?: 0)

		scriptBuilder.append("EXEC msdb.dbo.sp_add_alert").appendJbLn()
		scriptBuilder.addCommaWithNewLineScope().also { paramScope ->
			paramScope.invoke { scriptBuilder.append("@name = '", newName, "'") }
			paramScope.invoke { scriptBuilder.append("@message_id = ", DbUtils.getIntOrNull(newAlert.errorNumber)) }
			paramScope.invoke { scriptBuilder.append("@severity = ", DbUtils.getIntOrNull(newAlert.severity)) }
			paramScope.invoke { scriptBuilder.append("@enabled = ", DbUtils.getBoolOrNull(newAlert.isEnabled)) }
			paramScope.invoke { scriptBuilder.append("@delay_between_responses = ", delay) }
			paramScope.invoke { scriptBuilder.append("@notification_message = ", DbUtils.getQuotedOrNull(newAlert.notificationMessage)) }
			paramScope.invoke { scriptBuilder.append("@event_description_keyword = ", DbUtils.getQuotedOrNull(newAlert.messageText)) }
			paramScope.invoke { scriptBuilder.append("@include_event_description_in = ", DbUtils.getBoolOrNull(newAlert.includeEmail)) }
			paramScope.invoke { scriptBuilder.append("@database_name = ", DbUtils.getQuotedOrNull(newAlert.databaseName)) }
			paramScope.invoke { scriptBuilder.append("@job_id = ", DbUtils.getQuotedOrNull(newAlert.jobId)) }
			paramScope.invoke { scriptBuilder.append("@performance_condition = ", DbUtils.getQuotedOrNull(newAlert.performanceCondition)) }
			paramScope.invoke { scriptBuilder.append("@category_name = ", DbUtils.getQuotedOrNull(newAlert.categoryName)) }
			paramScope.invoke { scriptBuilder.append("@wmi_namespace = ", DbUtils.getQuotedOrNull(newAlert.wmiNamespace)) }
			paramScope.invoke { scriptBuilder.append("@wmi_query = ", DbUtils.getQuotedOrNull(newAlert.wmiQuery)) }
		}
		scriptBuilder.appendJbLn(";")

		for (operatorMod in model.operators) {
			val action = if (operatorMod.old.isSelected) "delete" else "add"
			scriptBuilder.append("EXEC msdb.dbo.sp_", action, "_notification").appendJbLn()
				.append("@alert_name = 'alert',").appendJbLn()
				.append("@operator_name = '", operatorMod.old.name, "',").appendJbLn()
				.appendJbLn("@notification_method = 1;")

		}

		return scriptBuilder
	}
	override fun getDropPart(model: MsAlertModel, scriptBuilder: StringBuilder): StringBuilder {
		return scriptBuilder.append("EXEC msdb.dbo.sp_delete_alert '", model.alert.old.name, "'")
	}

	override fun getAlterPart(model: MsAlertModel): String? = ""
}