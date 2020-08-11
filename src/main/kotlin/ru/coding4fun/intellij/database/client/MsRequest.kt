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

package ru.coding4fun.intellij.database.client

import com.intellij.database.datagrid.DataConsumer
import com.intellij.database.datagrid.DataRequest
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.jetbrains.rd.util.string.printToString
import ru.coding4fun.intellij.database.MsNotification
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.property.agent.MsNotifyLevel
import ru.coding4fun.intellij.database.model.property.agent.alert.AlertType
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditDestination
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditOnFailureKind
import ru.coding4fun.intellij.database.model.tree.MsKind
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.toMod
import java.util.*

class MsRequest(project: Project, sql: String) :
	DataRequest.RawQueryRequest(
		DataRequest.newOwnerEx(project),
		sql,
		DataRequest.newConstraints(0, 100_000, 0, 0)
	) {
	var onLastRowAdded: (() -> Unit)? = null
	val resultSetRows: MutableList<DataConsumer.Row> = LinkedList()
	var resultSetColumns: HashMap<String, Int> = hashMapOf()

	override fun addRows(context: Context, rows: MutableList<DataConsumer.Row>?) {
		if (rows?.any() == true) resultSetRows.addAll(rows)
	}

	override fun setColumns(
        context: Context,
        resultSetIndex: Int,
        columns: Array<out DataConsumer.Column>,
        firstRowNum: Int
    ) {
        columns.forEach { column ->
            resultSetColumns[column.name] = column.columnNum
        }
        super.setColumns(context, resultSetIndex, columns, firstRowNum)
    }

	override fun afterLastRowAdded(context: Context, total: Int) {
		super.afterLastRowAdded(context, total)
		onLastRowAdded?.invoke()
	}

	inline fun <reified T> getObjects(): List<T> {
		if (!resultSetRows.any()) return emptyList()
		val objects = ArrayList<T>(resultSetRows.size)

		val tConstructor = T::class.constructors.maxBy { it.parameters.size }!!
		val cParameterToColumnMap = tConstructor.parameters.map { cParameter -> resultSetColumns[cParameter.name] }

		for (resultSetRow in resultSetRows) {
			val cParameterValues = Array<Any?>(tConstructor.parameters.size) {}
			for (cParameterNumber: Int in tConstructor.parameters.indices) {
				val kParameter = tConstructor.parameters[cParameterNumber]
				val columnNumber = cParameterToColumnMap[cParameterNumber]
				if (columnNumber == null) {
					cParameterValues[cParameterNumber] = null
					continue
				}

				val cellValue = resultSetRow.values[columnNumber]
				if (cellValue == null) {
					cParameterValues[cParameterNumber] = null
				} else {
					cParameterValues[cParameterNumber] = when (kParameter.type.classifier) {
						MsNotifyLevel::class -> notifyLevelMapping[cellValue.toString()]
						MsKind::class -> MsKind.valueOf(cellValue.toString())
						MsServerAuditOnFailureKind::class -> MsServerAuditOnFailureKind.valueOf(cellValue.toString())
						MsServerAuditDestination::class -> MsServerAuditDestination.valueOf(cellValue.toString())
						AlertType::class -> alertTypeMapping[cellValue.toString()]!!
						else -> cellValue
					}
				}
			}
			try {
				val objectInstance = tConstructor.call(*cParameterValues)
				objects.add(objectInstance)
			} catch (e: Exception) {
				val errorMessage = "Unable to create an instance of the " + T::class.simpleName + "\n" +
						e.printToString()

				Logger.getInstance(MsRequest::class.java).error(errorMessage, e)
				MsNotification.error(errorMessage, errorMessage)
			}
		}

		return objects
	}

	inline fun <reified T> getModObject(): ModelModification<T> where T: Identity = getObjects<T>().first().toMod()

	companion object {
		val notifyLevelMapping = MsNotifyLevel.values().associateBy { it.id }
		val alertTypeMapping = AlertType.values().associateBy { it.id }
	}
}