package ru.coding4fun.intellij.database.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.GuiUtils
import ru.coding4fun.intellij.database.MsDialogWrapper
import ru.coding4fun.intellij.database.client.MsConnectionManager
import ru.coding4fun.intellij.database.data.property.security.ModelDataProvider
import ru.coding4fun.intellij.database.ui.form.ModelDialog
import ru.coding4fun.intellij.database.ui.form.MsSqlScriptPreview
import java.awt.Container
import java.util.function.Consumer
import javax.swing.JDialog

fun <Dialog, Model, DataProvider> displayDialog(
	dialog: Dialog,
	project: Project,
	dataProvider: DataProvider,
	modelId: String? = null
) where Dialog : JDialog,
		Dialog : ModelDialog<Model>,
		DataProvider : ModelDataProvider<Model>? {
	dataProvider?.getModel(modelId, Consumer { model ->
		val msDialog = MsDialogWrapper(project, dialog)
		dialog.addPropertyChangeListener("title") { msDialog.title = it.newValue.toString() }
		dialog.model = model
		dialog.activateSqlPreview { sqlPanel, eventPanels ->
			MsSqlScriptPreview.subscribeForChanges(
				project, MsConnectionManager.dbDataSource!!, dialog, dialog.scriptGenerator, sqlPanel, eventPanels
			)
		}
		msDialog.show()
	})
}

fun disableAll(parentContainer: Container) = GuiUtils.enableChildren(false, parentContainer)
