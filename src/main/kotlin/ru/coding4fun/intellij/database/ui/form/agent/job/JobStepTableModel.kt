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

package ru.coding4fun.intellij.database.ui.form.agent.job

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.property.agent.MsNotifyLevel
import ru.coding4fun.intellij.database.model.property.agent.job.MsJobStep
import ru.coding4fun.intellij.database.ui.form.common.MutableTableModel
import ru.coding4fun.intellij.database.ui.form.common.TableCellEditorFactory
import ru.coding4fun.intellij.database.ui.form.common.TableColumn
import ru.coding4fun.intellij.database.ui.form.common.TableColumnModel

class JobStepTableModel(subSystems: List<BasicIdentity>) :
    MutableTableModel<MsJobStep>(emptyList(), JobStepTableColumnModel(subSystems)) {
    private class JobStepTableColumnModel(subSystems: List<BasicIdentity>) : TableColumnModel<MsJobStep> {
        override val columns: Array<TableColumn<MsJobStep>>
            get() = arrayOf(number, name, type, onSuccess, onFailure)

        private val number: TableColumn<MsJobStep>
        private val name: TableColumn<MsJobStep>
        private val type: TableColumn<MsJobStep>
        private val onSuccess: TableColumn<MsJobStep>
        private val onFailure: TableColumn<MsJobStep>

        init {
            val notifyLevelCellEditor = TableCellEditorFactory.createComboBox(actions.values)
            val typeCellEditor = TableCellEditorFactory.createComboBox(subSystems)

            number = TableColumn(
                "Step",
                Int::class.javaObjectType,
                get = { model -> model.number },
                size = 50
            )
            name = TableColumn(
                "Name",
                String::class.javaObjectType,
                get = { model -> model.name },
                set = { model, aValue -> model.name = (aValue as String) },
                cellEditor = TableCellEditorFactory.createTextField()
            )
            onSuccess = TableColumn(
                "On Success",
                BasicIdentity::class.javaObjectType,
                get = { model -> actions[model.onSuccessAction]!! },
                set = { model, aValue -> model.onSuccessAction = (aValue as BasicIdentity).id.toShort() },
                cellEditor = notifyLevelCellEditor
            )
            onFailure = TableColumn(
                "On Failure",
                BasicIdentity::class.javaObjectType,
                get = { model -> actions[model.onFailureAction]!! },
                set = { model, aValue -> model.onFailureAction = (aValue as BasicIdentity).id.toShort() },
                cellEditor = notifyLevelCellEditor
            )
            type = TableColumn(
                "Type",
                String::class.javaObjectType,
                get = { model -> model.type },
                set = { model, aValue -> model.type = (aValue as BasicIdentity).name },
                cellEditor = typeCellEditor
            )
        }
    }

    companion object {
        private val actions: Map<Short, BasicIdentity> = arrayOf(
            MsNotifyLevel.Success,
            MsNotifyLevel.Failure,
            MsNotifyLevel.Completes
        ).map { BasicIdentity(it.id, it.actionDescription) }.associateBy { it.id.toShort() }
    }
}