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

package ru.coding4fun.intellij.database.ui.form

import com.intellij.database.psi.DbDataSource
import com.intellij.database.view.ui.SqlPreviewPanel
import com.intellij.openapi.project.Project
import com.intellij.ui.CheckBoxList
import com.intellij.ui.CheckboxTree
import com.intellij.ui.CheckboxTreeListener
import com.intellij.ui.CheckedTreeNode
import ru.coding4fun.intellij.database.extension.onKeyReleased
import ru.coding4fun.intellij.database.extension.onMouseClicked
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import java.awt.Component
import javax.swing.*
import javax.swing.SwingConstants.CENTER
import javax.swing.text.JTextComponent

object MsSqlScriptPreview {
	fun <Model> subscribeForChanges(
		project: Project,
		dbDataSource: DbDataSource,
		modelHolder: ModelDialog<Model>,
		scriptGenerator: ScriptGeneratorBase<Model>,
		sqlPreviewPanel: JPanel,
		panels: List<JPanel>,
		scriptState: MsSqlScriptState?
	) {
		val dgPreviewPanel = SqlPreviewPanel(project, dbDataSource, null, "", "mssql", false) {}
		sqlPreviewPanel.add(dgPreviewPanel, CENTER)

		fun updatePreview() {
			if (scriptState?.skipForUpdate == true) return
			val getScriptFun = if (modelHolder.isAlterMode) scriptGenerator::getAlterScript else scriptGenerator::getCreateScript
			dgPreviewPanel.query = getScriptFun.invoke(modelHolder.model)
		}
		if (scriptState != null) scriptState.updateScriptFun = ::updatePreview

		for (panel in panels) {
			subscribe(panel) { updatePreview() }
		}
	}

	private fun subscribe(component: Component, updatePreview: () -> Unit) {
		when (component) {
			is JTextComponent -> component.onKeyReleased { updatePreview() }
			is AbstractButton -> component.onMouseClicked { updatePreview() }
			is JComboBox<*> -> component.addActionListener { updatePreview() }
			is JList<*> -> component.onMouseClicked { updatePreview() }
			is JTable -> {
				component.onMouseClicked { updatePreview() }
				component.onKeyReleased { updatePreview() }
			}
			is CheckBoxList<*> -> component.setCheckBoxListListener { _, _ -> updatePreview() }
			is CheckboxTree -> component.addCheckboxTreeListener(object : CheckboxTreeListener {
				override fun nodeStateChanged(node: CheckedTreeNode) = updatePreview()
			})
			is JComponent -> component.components.forEach { subscribe(it) { updatePreview() } }
		}
	}
}