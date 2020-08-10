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

package ru.coding4fun.intellij.database.action

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.data.property.agent.AgentDataProviders
import ru.coding4fun.intellij.database.data.property.security.ModelDataProvider
import ru.coding4fun.intellij.database.data.property.security.SecurityDataProviders
import ru.coding4fun.intellij.database.model.tree.*
import ru.coding4fun.intellij.database.ui.displayDialog
import ru.coding4fun.intellij.database.ui.form.ModelDialog
import ru.coding4fun.intellij.database.ui.form.agent.*
import ru.coding4fun.intellij.database.ui.form.security.*
import javax.swing.JDialog

object PropertyHandler {
	fun openDialog(project: Project, selectedLabel: TreeLabel) {
		fun <Dialog, Model, DataProvider> display(
			dialog: Dialog,
			dataProvider: DataProvider
		)
				where Dialog : JDialog,
					  Dialog : ModelDialog<Model>,
					  DataProvider : ModelDataProvider<Model> {
			displayDialog(dialog, project, dataProvider, selectedLabel.id)
		}

		when {
//			// Security.
			selectedLabel.isLogin -> display(LoginDialog(), SecurityDataProviders.getLogin(project))
			selectedLabel.isCertificate -> display(CertificateDialog(), SecurityDataProviders.getCertificate(project))
			selectedLabel.isAsymmetricKey -> display(
				AsymmetricKeyDialog(),
				SecurityDataProviders.getAsymmetricKey(project)
			)
			selectedLabel.isCredential -> display(CredentialDialog(), SecurityDataProviders.getCredential(project))
			selectedLabel.isSymmetricKey -> display(
				SymmetricKeyDialog(),
				SecurityDataProviders.getSymmetricKey(project)
			)
			selectedLabel.isCryptographicProvider -> display(
				CryptographicProviderDialog(),
				SecurityDataProviders.getCryptographicProvider(project)
			)
			selectedLabel.isServerAuditSpecification -> display(
				ServerAuditSpecificationDialog(),
				SecurityDataProviders.getServerAuditSpecificationProvider(project)
			)
			selectedLabel.isServerAudit -> display(
				ServerAuditDialog(),
				SecurityDataProviders.getServerAuditProvider(project)
			)
			selectedLabel.isServerRole -> display(ServerRoleDialog(), SecurityDataProviders.getServerRole(project))
//			// Sql Server Agent.
			selectedLabel.isJob -> display(JobDialog(), AgentDataProviders.getJob(project))
			selectedLabel.isProxy -> display(ProxyDialog(), AgentDataProviders.getProxy(project))
			selectedLabel.isOperator -> display(OperatorDialog(), AgentDataProviders.getOperator(project))
			selectedLabel.isSchedule -> display(ScheduleDialog(), AgentDataProviders.getSchedule(project))
			selectedLabel.isAlert -> display(AlertDialog(), AgentDataProviders.getAlert(project))
			else -> Unit
		}
	}
}