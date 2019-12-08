package ru.coding4fun.intellij.database.data.property.agent

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project

object AgentDataProviders {
	fun getJob(project: Project): JobDataProvider = ServiceManager.getService(project, JobDataProvider::class.java)
	fun getOperator(project: Project): OperatorDataProvider =
		ServiceManager.getService(project, OperatorDataProvider::class.java)

	fun getSchedule(project: Project): ScheduleDataProvider =
		ServiceManager.getService(project, ScheduleDataProvider::class.java)

	fun getAlert(project: Project): AlertDataProvider =
		ServiceManager.getService(project, AlertDataProvider::class.java)
}