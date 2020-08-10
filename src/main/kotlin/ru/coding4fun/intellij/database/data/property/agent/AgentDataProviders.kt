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

package ru.coding4fun.intellij.database.data.property.agent

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project

object AgentDataProviders {
	//@formatter:off
	fun getJob(project: Project): JobDataProvider = ServiceManager.getService(project, JobDataProvider::class.java)
	fun getOperator(project: Project): OperatorDataProvider = ServiceManager.getService(project, OperatorDataProvider::class.java)
	fun getSchedule(project: Project): ScheduleDataProvider = ServiceManager.getService(project, ScheduleDataProvider::class.java)
	fun getAlert(project: Project): AlertDataProvider = ServiceManager.getService(project, AlertDataProvider::class.java)
	fun getProxy(project: Project): ProxyDataProvider = ServiceManager.getService(project, ProxyDataProvider::class.java)
	//@formatter:on
}