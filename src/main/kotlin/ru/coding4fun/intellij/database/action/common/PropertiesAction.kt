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

package ru.coding4fun.intellij.database.action.common

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.coding4fun.intellij.database.action.PropertyHandler
import ru.coding4fun.intellij.database.model.tree.*

class PropertiesAction : AnAction() {
	override fun actionPerformed(e: AnActionEvent) {
		val selectedLabel = e.getData(MsDataKeys.LABELS)!!.first()
		PropertyHandler.openDialog(e.project!!, selectedLabel)
	}

	override fun update(e: AnActionEvent) {
		val objects = e.getData(MsDataKeys.LABELS)!!

		var isVisible = false
		while (true) {
			val firstObject = (if (objects.size != 1) null else objects[0]) ?: break
			if (firstObject.kind.isFolder) break
			isVisible =
				// Security.
				firstObject.isLogin ||
						firstObject.isCertificate ||
						firstObject.isAsymmetricKey ||
						firstObject.isCredential ||
						firstObject.isSymmetricKey ||
						firstObject.isCryptographicProvider ||
						firstObject.isServerAuditSpecification ||
						firstObject.isServerAudit ||
						firstObject.isServerRole ||
						// Sql Server Agent.
						firstObject.isJob ||
						firstObject.isOperator ||
						firstObject.isSchedule ||
						firstObject.isAlert ||
						firstObject.isProxy
			break
		}

		e.presentation.isVisible = isVisible
	}
}