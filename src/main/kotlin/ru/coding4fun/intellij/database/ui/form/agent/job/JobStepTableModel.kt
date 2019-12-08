//package ru.coding4fun.intellij.database.ui.form.agent.job
//
//import ru.coding4fun.intellij.database.model.common.BasicIdentity
//import ru.coding4fun.intellij.database.model.property.agent.MsNotifyLevel
//import ru.coding4fun.intellij.database.model.property.agent.job.MsJobStep
//import ru.coding4fun.intellij.database.ui.form.common.MutableTableModel
//import ru.coding4fun.intellij.database.ui.form.common.TableColumn
//import ru.coding4fun.intellij.database.ui.form.common.TableColumnModel
//import javax.swing.DefaultCellEditor
//import javax.swing.JComboBox
//
//class JobStepTableModel : MutableTableModel<MsJobStep>(emptyList(), JobStepTableColumnModel) {
//	private object JobStepTableColumnModel : TableColumnModel<MsJobStep> {
//		override val columns: Array<TableColumn<MsJobStep>>
//			get() = arrayOf(number, name, type, onSuccess, onFailure)
//
//		private val number = TableColumn<MsJobStep>(
//			"Step",
//			Int::class.javaObjectType,
//			get = { model -> model.number },
//			size = 50
//		)
//
//		private val name = TableColumn<MsJobStep>(
//			"Name",
//			String::class.javaObjectType,
//			get = { model -> model.name }
//		)
//
//		private val type = TableColumn<MsJobStep>(
//			"Type",
//			String::class.javaObjectType,
//			get = { model -> model.type }
//		)
//
//		private val onSuccess = TableColumn<MsJobStep>(
//			"On Success",
//			BasicIdentity::class.javaObjectType,
//			get = { model -> actions[model.onSuccessAction]!! },
//			set = { model, aValue -> model.onSuccessAction = (aValue as BasicIdentity).id.toShort() },
//			cellEditor = notifyLevelCellEditor
//		)
//
//		private val onFailure = TableColumn<MsJobStep>(
//			"On Failure",
//			BasicIdentity::class.javaObjectType,
//			get = { model -> actions[model.onFailureAction]!! },
//			set = { model, aValue -> model.onFailureAction = (aValue as BasicIdentity).id.toShort() },
//			cellEditor = notifyLevelCellEditor
//		)
//	}
//
//	companion object {
//		private val actions: Map<Short, BasicIdentity> = arrayOf(
//			MsNotifyLevel.Success,
//			MsNotifyLevel.Failure,
//			MsNotifyLevel.Completes
//		).map { BasicIdentity(it.id.toString(), it.actionDescription) }.associateBy { it.id.toShort() }
//
//		private val notifyLevelCellEditor: DefaultCellEditor
//
//		init {
//			val comboBox = JComboBox<BasicIdentity>()
//			actions.forEach { comboBox.addItem(it.value) }
//			notifyLevelCellEditor = DefaultCellEditor(comboBox)
//		}
//	}
//}