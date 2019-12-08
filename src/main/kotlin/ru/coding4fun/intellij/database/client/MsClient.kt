package ru.coding4fun.intellij.database.client

import com.intellij.database.dataSource.AsyncUtil
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.intellij.openapi.project.Project
import org.apache.commons.lang.text.StrSubstitutor
import java.util.concurrent.Semaphore
import java.util.function.Consumer

abstract class MsClient(val project: Project) {
	private val separator = ":^%^:"
	private val logger = Logger.getInstance(MsClient::class.java)

	fun separateId(objectId: String): List<String> = objectId.split(separator, ignoreCase = false, limit = 0)

	protected fun invokeComposite(
		taskDesc: String,
		queryDefinitions: List<QueryDefinition>,
		successHandler: Consumer<Unit>
	) {
		var hasErrors = false
		val task = object : Task.Backgroundable(project, taskDesc) {
			override fun run(indicator: ProgressIndicator) {
				AsyncUtil.markAsyncFriendly(indicator, true)

				var queryNumber = 0.0
				for (queryDefinition in queryDefinitions) {
					queryNumber += 1.0
					indicator.text = queryDefinition.progressDesc
					indicator.fraction = queryDefinitions.size.toDouble() / queryNumber
					val request = invokeDbRequest(queryDefinition) { hasErrors = true }
					if (hasErrors) break
					queryDefinition.consumer.accept(request)
				}
				indicator.stop()
			}

			override fun onSuccess() {
				super.onSuccess()
				successHandler.accept(Unit)
			}
		}

		val processIndicator = BackgroundableProcessIndicator(task)
		processIndicator.isIndeterminate = false
		processIndicator.start()
		ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, processIndicator)
	}

	fun invokeDbRequest(
		queryDefinition: QueryDefinition,
		errorHandler: ((Throwable) -> Unit)
	): MsRequest {
		var sql = MsResourceUtil.readQuery(queryDefinition.resPath)
		if (queryDefinition.queryParameters?.any() == true) {
			val stringSubstitutor = StrSubstitutor(queryDefinition.queryParameters, "??", "??")
			sql = stringSubstitutor.replace(sql)
		}
		val request = MsRequest(project, sql)
		val semaphore = Semaphore(0, true)
		request.onLastRowAdded = {
			semaphore.release()
		}
		request.promise.onError {
			logger.error(sql, it)
			errorHandler(it)
			Notifications.Bus.notify(
				Notification(
					"MsSql",
					"Unable to execute request",
					sql,
					NotificationType.ERROR
				)
			)
			semaphore.release()
		}
		MsConnectionManager.client!!.messageBus.dataProducer.processRequest(request)
		semaphore.acquire()

		return request
	}
}