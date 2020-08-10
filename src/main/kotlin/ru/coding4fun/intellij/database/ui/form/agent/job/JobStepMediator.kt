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

import ru.coding4fun.intellij.database.extension.onMouseClicked
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.property.agent.job.MsJobModel
import ru.coding4fun.intellij.database.model.property.agent.job.MsJobStep
import ru.coding4fun.intellij.database.model.property.agent.job.MsJobStepFlags
import ru.coding4fun.intellij.database.ui.form.MsSqlScriptState
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.ModificationTracker
import ru.coding4fun.intellij.database.ui.form.common.toModNew
import ru.coding4fun.intellij.database.ui.form.state.CheckBoxGetter
import ru.coding4fun.intellij.database.ui.form.state.ComboBoxGetter
import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter
import ru.coding4fun.intellij.database.ui.synchronizeById
import ru.coding4fun.intellij.database.ui.synchronizeByName
import java.util.*
import javax.swing.*

class JobStepMediator(
    private val model: MsJobModel,
    private val scriptState: MsSqlScriptState,
    private val stepTable: JTable,
    private val stepDatabaseComboBox: JComboBox<BasicIdentity>,
    private val commandTextArea: JTextArea,
    private val useProxyCheckBox: JCheckBox,
    private val proxyComboBox: JComboBox<BasicIdentity>,
    private val retryAttemptsTextField: JTextField,
    private val retryIntervalTextField: JTextField,
    private val outputFileTextField: JTextField,
    private val outputFileComboBox: JComboBox<BasicIdentity>,
    private val logTableCheckBox: JCheckBox,
    private val logTableComboBox: JComboBox<BasicIdentity>,
    private val stepHistoryCheckBox: JCheckBox,
    private val jobHistoryCheckBox: JCheckBox,
    private val abortEventCheckBox: JCheckBox,
    private val stepPanel: JPanel,
    private val startStepComboBox: JComboBox<BasicIdentity>
) : ModificationTracker<MsJobStep> {
    private val stepModifications: HashMap<String, ModelModification<MsJobStep>>
    val tableModel: JobStepTableModel

    init {
        stepModifications = model.steps
            .map { it.toModNew() }
            .associateBy { step -> step.old.id }
            .toMap(hashMapOf())

        tableModel = JobStepTableModel(model.subSystems).also {
            it.rows = stepModifications.values.map { mod -> mod.new!! }.sortedBy { step -> step.number }.toMutableList()
            stepTable.model = it
            it.initColumnSettings(stepTable)
            it.addOnValueChangeAction {
                renderSelectedStep()
            }
        }

        stepTable.onMouseClicked {
            renderSelectedStep()
        }

        updateStartStep()
    }

    private fun updateStartStep() {
        model.job.new?.startStepId = null
        if (tableModel.rowCount > 0) {
            val simpleJobSteps: List<BasicIdentity> = stepModifications
                .map { it.value.new!! }
                .map { BasicIdentity(it.number.toString(), it.name) }

            val startStep = (tableModel.getRow(model.job.old.startStepId?.toInt()?.minus(1) ?: 0)?.number ?: 0).toString()
            model.job.new?.startStepId = startStep
            startStepComboBox.synchronizeById(simpleJobSteps, startStep, null)
        } else {
            startStepComboBox.synchronizeById(emptyList(), null, null)
        }
    }

    override fun getModifications(): List<ModelModification<MsJobStep>> {
        updateSelectedStepFromUi()
        return stepModifications.filter { it.value.isModified }.values.toList()
    }

    private var uiInAction: Boolean = false
    private fun invokeUiUpdate(action: () -> Unit) {
        if (uiInAction) return
        uiInAction = true
        scriptState.invokeUpdate(action)
        uiInAction = false
    }

    private fun renderSelectedStep() {
        val step = selectedStep ?: return
        invokeUiUpdate {
            commandTextArea.text = step.command

            stepDatabaseComboBox.synchronizeByName(model.databases, step.dbName, null)
            proxyComboBox.synchronizeByName(model.proxies, step.proxyName, useProxyCheckBox)
            retryAttemptsTextField.text = step.retryAttempts.toString()
            retryIntervalTextField.text = step.retryInterval.toString()
            outputFileTextField.text = step.outputFile
            //logTableCheckBox
            stepHistoryCheckBox.isSelected = step.stepHistory
            jobHistoryCheckBox.isSelected = step.jobHistory
            abortEventCheckBox.isSelected = step.abortEvent
            var selectedOutputFile = "-1"
            if (step.appendFile) {
                selectedOutputFile = MsJobStepFlags.AppendFile.id.toString()
            } else if (step.overrideFile) {
                selectedOutputFile = MsJobStepFlags.OverrideFile.id.toString()
            }
            var selectedLogTableFile = "-1"
            if (step.appendTable) {
                selectedLogTableFile = MsJobStepFlags.AppendTable.id.toString()
            } else if (step.overrideTable) {
                selectedLogTableFile = MsJobStepFlags.OverrideTable.id.toString()
            }
            outputFileComboBox.synchronizeById(outputFilesOptions, selectedOutputFile, null)
            logTableComboBox.synchronizeById(logTableOptions, selectedLogTableFile, logTableCheckBox)

            stepDatabaseComboBox.isEnabled = listOf("TSQL", "QueueReader").contains(step.type)
        }
    }

    private fun updateSelectedStepFromUi() {
        val step = selectedStep ?: return
        invokeUiUpdate {
            val fileOptionId = ComboBoxGetter.getSelected(outputFileComboBox, BasicIdentity::id) ?: "-1"
            val tableOptionId = ComboBoxGetter.getSelected(logTableComboBox, BasicIdentity::id) ?: "-1"
            val appendFile = containsBit(MsJobStepFlags.AppendFile, fileOptionId)

            step.onSuccessAction = selectedStep?.onSuccessAction ?: 0
            step.onFailureAction = selectedStep?.onFailureAction ?: 0
            step.command = TextFieldGetter.getTextOrCompute(commandTextArea) { "" }
            step.dbName = ComboBoxGetter.getText(stepDatabaseComboBox)
            step.proxyName = ComboBoxGetter.getText(proxyComboBox)
            step.retryAttempts = TextFieldGetter.getIntOrCompute(retryAttemptsTextField) { 0 }
            step.retryInterval = TextFieldGetter.getIntOrCompute(retryIntervalTextField) { 0 }
            step.outputFile = TextFieldGetter.getText(outputFileTextField)
            step.appendFile = appendFile && fileOptionId != "-1"
            step.overrideFile = !appendFile && fileOptionId != "-1"
            step.stepHistory = CheckBoxGetter.apply(stepHistoryCheckBox)
            step.jobHistory = CheckBoxGetter.apply(jobHistoryCheckBox)
            step.abortEvent = CheckBoxGetter.apply(abortEventCheckBox)
            step.proxyName = ComboBoxGetter.getText(proxyComboBox)
            step.appendTable = containsBit(MsJobStepFlags.AppendTable, tableOptionId)
            step.overrideTable = containsBit(MsJobStepFlags.OverrideTable, tableOptionId)
        }
    }

    fun addStep() {
        val stepId = UUID.randomUUID().toString()
        val newStep = MsJobStep(
            stepId,
            "My step",
            stepModifications.size + 1,
            "TSQL",
            1,
            2,
            "PRINT 'Hello World'",
            "master",
            null,
            0,
            0,
            null,
            false,
            false,
            true,
            true,
            false,
            false,
            false,
            ""
        )
        stepModifications[stepId] = ModelModification(newStep.getCopy(), newStep)
        newStep.name = "" // Force to model.IsModified == true.
        tableModel.addRow(newStep)

        updateStartStep()
    }

    fun upStep() {
        val prevRow = stepTable.selectedRow - 1
        tableModel.rows[stepTable.selectedRow].number -= 1
        tableModel.rows[prevRow].number += 1
        tableModel.upRow(stepTable.selectedRow)
        stepTable.setRowSelectionInterval(prevRow, prevRow)

        updateStartStep()
    }

    fun downStep() {
        val nextRow = stepTable.selectedRow + 1
        tableModel.rows[stepTable.selectedRow].number += 1
        tableModel.rows[nextRow].number -= 1
        tableModel.downRow(stepTable.selectedRow)
        stepTable.setRowSelectionInterval(nextRow, nextRow)

        updateStartStep()
    }

    fun restoreStep() {
        val selectedStep = tableModel.rows[stepTable.selectedRow]
        val stepMod = stepModifications[selectedStep.id]!!
        val number = stepMod.new!!.number
        val newStep = stepMod.old.getCopy()
        newStep.number = number
        stepMod.new = newStep
        tableModel.rows[stepTable.selectedRow] = newStep
        renderSelectedStep()
        stepTable.invalidate()
    }

    fun deleteStep() {
        stepModifications[selectedStep!!.id]!!.new = null
        tableModel.removeRow(stepTable.selectedRow)

        updateStartStep()
    }

    private val selectedStep: MsJobStep? get() = tableModel.getRow(stepTable.selectedRow)

    private fun containsBit(expected: MsJobStepFlags, id: String) = (id.toInt().and(expected.id)) == expected.id

    private val outputFilesOptions = listOf(
        MsJobStepFlags.OverrideFile,
        MsJobStepFlags.AppendFile
    ).map { option -> BasicIdentity(option.id.toString(), option.title!!) }

    private val logTableOptions = listOf(
        MsJobStepFlags.OverrideTable,
        MsJobStepFlags.AppendTable
    ).map { option -> BasicIdentity(option.id.toString(), option.title!!) }
}