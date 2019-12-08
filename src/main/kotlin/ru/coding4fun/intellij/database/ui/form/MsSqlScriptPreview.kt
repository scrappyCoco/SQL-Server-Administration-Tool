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
		panels: List<JPanel>
	) {
		val dgPreviewPanel = SqlPreviewPanel(project, dbDataSource, null, "", "mssql", false) {}
		sqlPreviewPanel.add(dgPreviewPanel, CENTER)

		fun updatePreview() {
			dgPreviewPanel.query = if (modelHolder.isAlterMode)
				scriptGenerator.getAlterScript(modelHolder.model)
			else scriptGenerator.getCreateScript(modelHolder.model)
		}

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