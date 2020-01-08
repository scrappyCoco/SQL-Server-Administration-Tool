package ru.coding4fun.intellij.database

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import ru.coding4fun.intellij.database.action.common.ScriptActionUtil
import ru.coding4fun.intellij.database.ui.form.ModelDialog
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.WindowConstants

class MsDialogWrapper<Model, Dialog>(
    private val project: Project,
    private val delegatedDialog: Dialog
) : DialogWrapper(project),
    ModelDialog<Model> by delegatedDialog
        where Dialog : JDialog, Dialog : ModelDialog<Model> {
    init {
        init()
        title = "Simple"
		delegatedDialog.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    }

    override fun createCenterPanel(): JComponent? = delegatedDialog.contentPane as JComponent
    override fun getDimensionServiceKey(): String? = delegatedDialog.dialogId
    override fun getHelpId(): String? = delegatedDialog.modelHelpId

    override fun doOKAction() {
        ScriptActionUtil.openSqlDocument(delegatedDialog, this.project);
        super.doOKAction()
    }
}