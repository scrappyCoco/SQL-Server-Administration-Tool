package ru.coding4fun.intellij.database.client

import com.intellij.database.datagrid.DataConsumer
import com.intellij.database.datagrid.DataRequest
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.model.property.agent.MsNotifyLevel
import ru.coding4fun.intellij.database.model.property.agent.alert.AlertType
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditDestination
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditOnFailureKind
import ru.coding4fun.intellij.database.model.tree.MsKind
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
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
		infos: Array<out DataConsumer.Column>?,
		firstRowNum: Int
	) {
		infos?.forEach { column ->
			resultSetColumns[column.name] = column.columnNum
		}
		super.setColumns(context, resultSetIndex, infos, firstRowNum)
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
						MsNotifyLevel::class -> notifyLevelMapping[(cellValue as Short).toByte()]
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
				val errorMessage = "Unable to create class " + T::class.simpleName
				Logger.getInstance(MsRequest::class.java).error(errorMessage, e)
				Notifications.Bus.notify(Notification("MsSql", "MS SQL Error", errorMessage, NotificationType.ERROR))
			}
		}

		return objects
	}

	inline fun <reified T> getModObject(): ModelModification<T> = ModelModification(getObjects<T>().firstOrNull(), null)

	companion object {
		val notifyLevelMapping = MsNotifyLevel.values().associateBy { it.id }
		val alertTypeMapping = AlertType.values().associateBy { it.id }
	}
}