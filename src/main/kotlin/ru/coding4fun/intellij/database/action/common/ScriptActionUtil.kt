package ru.coding4fun.intellij.database.action.common

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.util.ResourceUtil
import ru.coding4fun.intellij.database.action.script.InvokeKind
import ru.coding4fun.intellij.database.data.property.agent.AgentDataProviders
import ru.coding4fun.intellij.database.data.property.security.ModelDataProvider
import ru.coding4fun.intellij.database.data.property.security.SecurityDataProviders
import ru.coding4fun.intellij.database.extension.appendLnIfAbsent
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.generation.agent.JobGenerator
import ru.coding4fun.intellij.database.generation.agent.OperatorGenerator
import ru.coding4fun.intellij.database.generation.agent.ScheduleGenerator
import ru.coding4fun.intellij.database.generation.security.*
import ru.coding4fun.intellij.database.model.tree.*
import ru.coding4fun.intellij.database.ui.form.ModelDialog
import java.util.*
import java.util.function.Consumer

object ScriptActionUtil {
	fun updateVisibility(e: AnActionEvent, labelKinds: Set<MsKind>) {
		val labels = e.getData(MsDataKeys.LABELS)!!

		e.presentation.isVisible = false

		for (label in labels) {
			if (labelKinds.contains(label.kind)) {
				e.presentation.isVisible = true
				return
			}

			return
		}
	}

	fun openSqlDocument(sqlCode: String, project: Project) {
		val psiFileFactory = PsiFileFactory.getInstance(project)
		val fileEditorManager = FileEditorManager.getInstance(project)

		val psiFile = psiFileFactory.createFileFromText(MsDialect.INSTANCE, sqlCode)
		psiFile.virtualFile.rename(null, "generated_" + UUID.randomUUID().toString().substring(0, 8) + ".sql")

		fileEditorManager.openFile(psiFile.virtualFile, true)
	}


	fun openScriptByResourceHandler(event: AnActionEvent, handlers: Map<MsKind, ResourceActionHandler>) {
		val selectedLabel = event.getData(MsDataKeys.LABELS)!!.first()
		val actionHandler = handlers.getValue(selectedLabel.kind)
		val resourceUri = actionHandler.resource
		val filedValue = actionHandler.fieldToSubstitute.invoke(selectedLabel)
		val script = substituteResource(resourceUri, filedValue)
		openSqlDocument(script, event.project!!)
	}

	private fun substituteResource(resourceUri: String, objectId: String): String {
		val errorLogScriptUrl = ResourceUtil.getResource(ViewAction::class.java, "/", resourceUri)
		val queryTemplate = ResourceUtil.loadText(errorLogScriptUrl)
		return String.format(queryTemplate, objectId)
	}


	fun <Model> openSqlDocument(
		modelDialog: ModelDialog<Model>,
		project: Project
	) {
		val script = if (modelDialog.isAlterMode) {
			modelDialog.scriptGenerator.getAlterScript(modelDialog.model)
		} else modelDialog.scriptGenerator.getCreateScript(modelDialog.model)

		openSqlDocument(script, project)
	}

	fun openScriptInEditor(
		project: Project,
		objects: Array<TreeLabel>,
		invokeKind: InvokeKind
	) {
		val scriptBuilder = StringBuilder()

		for (obj in objects.take(1)) {
			fun <Model> create(
				modelDataProvider: ModelDataProvider<Model>,
				modelGeneratorBase: ScriptGeneratorBase<Model>
			) {
				modelDataProvider.getModel(obj.id, Consumer { model ->
					val scriptText = when (invokeKind) {
						InvokeKind.CREATE -> modelGeneratorBase.getCreateScript(model, true)
						InvokeKind.DROP -> modelGeneratorBase.getDropScript(model)
						InvokeKind.DROP_AND_CREATE -> modelGeneratorBase.getDropAndCreateScript(model)
					}
					scriptBuilder.appendLnIfAbsent()
					scriptBuilder.append(scriptText)
					openSqlDocument(scriptBuilder.toString(), project)
				})
			}

			when {
				obj.isLogin -> create(SecurityDataProviders.getLogin(project), LoginGenerator)
				obj.isCredential -> create(SecurityDataProviders.getCredential(project), CredentialGenerator)
				obj.isCertificate -> create(SecurityDataProviders.getCertificate(project), CertificateGenerator)
				obj.isAsymmetricKey -> create(SecurityDataProviders.getAsymmetricKey(project), AsymmetricKeyGenerator)
				obj.isCryptographicProvider -> create(
					SecurityDataProviders.getCryptographicProvider(project),
					CryptographicProviderGenerator
				)
				obj.isSymmetricKey -> create(SecurityDataProviders.getSymmetricKey(project), SymmetricKeyGenerator)
				obj.isServerAuditSpecification -> create(
					SecurityDataProviders.getServerAuditSpecificationProvider(project),
					ServerAuditSpecificationGenerator
				)
				obj.isServerRole -> create(SecurityDataProviders.getServerRole(project), ServerRoleGenerator)
				obj.isServerAudit -> create(SecurityDataProviders.getServerAuditProvider(project), ServerAuditGenerator)
				obj.isOperator -> create(AgentDataProviders.getOperator(project), OperatorGenerator)
				obj.isSchedule -> create(AgentDataProviders.getSchedule(project), ScheduleGenerator)
				obj.isJob -> create(AgentDataProviders.getJob(project), JobGenerator)
				else -> Unit
			}
		}
	}

	fun updateByResourceHandler(event: AnActionEvent, actionHandlers: HashMap<MsKind, ResourceActionHandler>) {
		val selectedLabel = event.getData(MsDataKeys.LABELS)!!.first()
		val labelKind = selectedLabel.kind
		val actionHandler = actionHandlers.get(labelKind)
		if (actionHandler != null) {
			event.presentation.isVisible = true
			event.presentation.text = actionHandlers[labelKind]!!.text
			if (actionHandler.updateAction != null) actionHandler.updateAction.invoke(selectedLabel, event)

		} else {
			event.presentation.isVisible = false
		}
	}
}