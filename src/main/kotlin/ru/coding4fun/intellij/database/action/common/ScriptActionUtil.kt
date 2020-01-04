package ru.coding4fun.intellij.database.action.common

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.sql.dialects.mssql.MsDialect
import com.intellij.util.ResourceUtil
import ru.coding4fun.intellij.database.MsNotification
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
import java.util.concurrent.Semaphore
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

    private interface ScriptModelDescriptor {
        fun addObject(treeLabel: TreeLabel)
        fun executeQuery(invokeKind: InvokeKind): Array<String>
    }

    private class ScriptModelDescriptorImpl<Model>(
        val modelDataProvider: ModelDataProvider<Model>,
        val modelGeneratorBase: ScriptGeneratorBase<Model>,
        val isTargetType: ((TreeLabel) -> Boolean)
    ) : ScriptModelDescriptor {
        private val objects = LinkedList<String>()

        override fun addObject(treeLabel: TreeLabel) {
            if (!isTargetType(treeLabel)) return
            objects.addLast(treeLabel.id)
        }

        override fun executeQuery(invokeKind: InvokeKind): Array<String> {
            if (!objects.any()) return emptyArray()
            val scripts = LinkedList<String>()

            val semaphore = Semaphore(0, true)
            try {
                modelDataProvider.getModels(
                    objects.toTypedArray(),
                    Consumer { modelsMap ->
                        try {
                            for (model in modelsMap.values) {
                                scripts.addLast(
                                    when (invokeKind) {
                                        InvokeKind.CREATE -> modelGeneratorBase.getCreateScript(model, true)
                                        InvokeKind.DROP -> modelGeneratorBase.getDropScript(model)
                                        InvokeKind.DROP_AND_CREATE -> modelGeneratorBase.getDropAndCreateScript(model)
                                    }
                                )
                            }
                        } catch (e: Exception) {
                            handleException(e)
                        } finally {
                            semaphore.release()
                        }
                    }, Consumer {
                        handleException(it)
                        semaphore.release()
                    }
                )
                semaphore.acquire()
            } catch (e: Exception) {
                handleException(e)
                semaphore.release()
            }

            return scripts.toTypedArray()
        }
    }

    private fun handleException(e: Exception) {
        MsNotification.error("Unable to create script", e.message ?: "Exception message is empty")
    }

    fun openScriptInEditor(
        project: Project,
        objects: Array<TreeLabel>,
        invokeKind: InvokeKind
    ) {
        val scriptBuilder = StringBuilder()

        //@formatter:off
        val modelDescriptors: List<ScriptModelDescriptor> = listOf(
			ScriptModelDescriptorImpl(SecurityDataProviders.getLogin(project), LoginGenerator) { it.isLogin },
			ScriptModelDescriptorImpl(SecurityDataProviders.getCredential(project), CredentialGenerator) { it.isCredential },
			ScriptModelDescriptorImpl(SecurityDataProviders.getCertificate(project), CertificateGenerator) { it.isCertificate },
			ScriptModelDescriptorImpl(SecurityDataProviders.getAsymmetricKey(project), AsymmetricKeyGenerator) { it.isAsymmetricKey },
			ScriptModelDescriptorImpl(SecurityDataProviders.getCryptographicProvider(project), CryptographicProviderGenerator) { it.isCryptographicProvider },
			ScriptModelDescriptorImpl(SecurityDataProviders.getSymmetricKey(project), SymmetricKeyGenerator) { it.isSymmetricKey },
			ScriptModelDescriptorImpl(SecurityDataProviders.getServerAuditSpecificationProvider(project), ServerAuditSpecificationGenerator) { it.isServerAuditSpecification },
			ScriptModelDescriptorImpl(SecurityDataProviders.getServerRole(project), ServerRoleGenerator) { it.isServerRole },
			ScriptModelDescriptorImpl(SecurityDataProviders.getServerAuditProvider(project), ServerAuditGenerator) { it.isServerAudit },
			ScriptModelDescriptorImpl(AgentDataProviders.getOperator(project), OperatorGenerator) { it.isOperator },
			ScriptModelDescriptorImpl(AgentDataProviders.getSchedule(project), ScheduleGenerator) { it.isSchedule },
			ScriptModelDescriptorImpl(AgentDataProviders.getJob(project), JobGenerator) { it.isJob }
        )
        //@formatter:on

        for (obj in objects) {
            for (modelDescriptor in modelDescriptors) {
                modelDescriptor.addObject(obj)
            }
        }

        for (modelDescriptor in modelDescriptors) {
            val objectScripts = modelDescriptor.executeQuery(invokeKind)
            for (objectScript in objectScripts) {
                scriptBuilder.appendLnIfAbsent().append(objectScript)
            }
        }
        openSqlDocument(scriptBuilder.toString(), project)
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