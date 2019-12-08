package ru.coding4fun.intellij.database.action

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.data.property.agent.AgentDataProviders
import ru.coding4fun.intellij.database.data.property.security.ModelDataProvider
import ru.coding4fun.intellij.database.data.property.security.SecurityDataProviders
import ru.coding4fun.intellij.database.model.tree.*
import ru.coding4fun.intellij.database.ui.displayDialog
import ru.coding4fun.intellij.database.ui.form.ModelDialog
import ru.coding4fun.intellij.database.ui.form.agent.AlertDialog
import ru.coding4fun.intellij.database.ui.form.agent.JobDialog
import ru.coding4fun.intellij.database.ui.form.agent.OperatorDialog
import ru.coding4fun.intellij.database.ui.form.agent.ScheduleDialog
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
			selectedLabel.isOperator -> display(OperatorDialog(), AgentDataProviders.getOperator(project))
			selectedLabel.isSchedule -> display(ScheduleDialog(), AgentDataProviders.getSchedule(project))
			selectedLabel.isAlert -> display(AlertDialog(), AgentDataProviders.getAlert(project))
			else -> Unit
		}
	}
}