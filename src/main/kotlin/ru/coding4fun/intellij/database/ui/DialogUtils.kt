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

package ru.coding4fun.intellij.database.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.ui.GuiUtils
import ru.coding4fun.intellij.database.MsDialogWrapper
import ru.coding4fun.intellij.database.client.MsConnectionManager
import ru.coding4fun.intellij.database.data.property.DbUtils
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
    val objectIds = if (modelId == null) null else arrayOf(modelId)
    dataProvider?.getModels(
        objectIds,
        Consumer { models ->
            ApplicationManager.getApplication().invokeLater {
                val msDialog = MsDialogWrapper(project, dialog)
                dialog.addPropertyChangeListener("title") { msDialog.title = it.newValue.toString() }
                dialog.model = models[modelId ?: DbUtils.defaultId]!!

                dialog.activateSqlPreview { sqlPanel, eventPanels, scriptState ->
                     MsSqlScriptPreview.subscribeForChanges(
                        project,
                        MsConnectionManager.dbDataSource!!,
                        dialog,
                        dialog.scriptGenerator,
                        sqlPanel,
                        eventPanels,
                         scriptState
                    )
                }
                msDialog.show()
            }
        }, Consumer {})
}

fun disableAll(parentContainer: Container) = GuiUtils.enableChildren(false, parentContainer)
