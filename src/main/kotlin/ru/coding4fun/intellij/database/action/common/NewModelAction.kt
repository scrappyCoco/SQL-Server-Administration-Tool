package ru.coding4fun.intellij.database.action.common

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.data.property.security.ModelDataProvider
import ru.coding4fun.intellij.database.model.tree.MsKind
import ru.coding4fun.intellij.database.ui.displayDialog
import ru.coding4fun.intellij.database.ui.form.ModelDialog
import javax.swing.JDialog

abstract class NewModelAction<Model, Dialog>(
	private val targetKinds: Set<MsKind>,
	private val createDialog: (() -> Dialog),
	private val dataProviderFun: ((Project) -> ModelDataProvider<Model>)
): AnAction()
	where Dialog: ModelDialog<Model>,
		  Dialog: JDialog {
	override fun actionPerformed(e: AnActionEvent) = displayDialog(createDialog.invoke(), e.project!!, dataProviderFun(e.project!!), null)
	override fun update(e: AnActionEvent) = ScriptActionUtil.updateVisibility(e, targetKinds)
}