//package ru.coding4fun.intellij.database.ui.form.agent.job
//
//import ru.coding4fun.intellij.database.extension.onMouseClicked
//import ru.coding4fun.intellij.database.model.property.agent.job.MsJobStep
//import ru.coding4fun.intellij.database.ui.form.common.ModelModification
//import ru.coding4fun.intellij.database.ui.form.common.ModificationTracker
//import ru.coding4fun.intellij.database.ui.form.common.toModificationList
//import java.beans.PropertyChangeListener
//import java.util.*
//import javax.swing.JComponent
//import javax.swing.JTable
//
//class JobStepMediator(
//	private val stepTable: JTable,
//	steps: List<MsJobStep>,
//	private val updateStepToUi: ((MsJobStep) -> Unit),
//	private val getStepFromUi: (() -> MsJobStep),
//	vararg stepComponents: JComponent
//) : ModificationTracker<MsJobStep> {
//	private val stepModifications: HashMap<String, ModelModification<MsJobStep>>
//	private var currentStepRow: Int? = null
//
//	init {
//		stepModifications = steps.toModificationList()
//			.associateBy({ step -> step.new.id }) { step -> step }
//			.toMap(hashMapOf())
//		val tableModel = JobStepTableModel()
//		tableModel.rows = steps
//		stepTable.model = tableModel
//		stepTable.onMouseClicked { updateTableStepInfo() }
//		tableModel.initColumnSettings(stepTable)
//		tableModel.addOnValueChangeAction { this.saveStepModification(it) }
//
//		for (stepComponent in stepComponents) {
//			stepComponent.addPropertyChangeListener {
//				PropertyChangeListener {
//					saveStepModification(ModelModification(null, getStepFromUi()))
//				}
//			}
//		}
//	}
//
//	override fun getModifications(): List<ModelModification<MsJobStep>> {
//		return stepModifications.filter { (it.value.old?.hashCode() ?: -1) != it.value.new.hashCode() }.values.toList()
//	}
//
//	private fun saveStepModification(modification: ModelModification<MsJobStep>) {
//		if (!stepModifications.containsKey(modification.new.id)) {
//			stepModifications[modification.new.id] = modification
//		} else {
//			stepModifications[modification.new.id]!!.new = modification.new
//		}
//	}
//
//	private fun updateTableStepInfo() {
//		val selectedStepRow = stepTable.selectedRow
//		if (selectedStepRow == currentStepRow) return
//
//		val tableModel = stepTable.model as JobStepTableModel
//		val step = tableModel.rows[selectedStepRow]
//
//		updateStepToUi(step)
//	}
//}