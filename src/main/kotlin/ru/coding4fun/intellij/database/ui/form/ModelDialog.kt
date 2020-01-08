package ru.coding4fun.intellij.database.ui.form

import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import javax.swing.JPanel

interface ModelDialog<Model> {
	val dialogId: String
	var model: Model
	val isAlterMode: Boolean
	val scriptGenerator: ScriptGeneratorBase<Model>
	val modelHelpId: String?
	fun activateSqlPreview(activateFun: ((sqlPreviewPanel: JPanel, eventPanels: List<JPanel>) -> Unit))
}