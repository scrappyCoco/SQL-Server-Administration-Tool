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

package ru.coding4fun.intellij.database.ui.form.agent;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.agent.OperatorGenerator;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.property.agent.MsOperator;
import ru.coding4fun.intellij.database.model.property.agent.operator.MsOperatorAlert;
import ru.coding4fun.intellij.database.model.property.agent.operator.MsOperatorJob;
import ru.coding4fun.intellij.database.model.property.agent.operator.MsOperatorModel;
import ru.coding4fun.intellij.database.ui.JComboBoxUtilKt;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.agent.operator.OperatorJobTableModel;
import ru.coding4fun.intellij.database.ui.form.common.CheckBoxListModTracker;
import ru.coding4fun.intellij.database.ui.form.common.ModificationTracker;
import ru.coding4fun.intellij.database.ui.form.common.TableModificationTracker;
import ru.coding4fun.intellij.database.ui.form.state.CheckBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.ComboBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter;

import javax.swing.*;
import java.util.List;

public class OperatorDialog extends JDialog implements ModelDialog<MsOperatorModel> {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JLabel nameLabel;
	private JTextField nameTextField;
	private JCheckBox enabledCheckBox;
	private JTextField emailTextField;
	private JLabel emailLabel;
	private JTable jobTable;
	private JLabel alertsLabel;
	private JLabel jobsLabel;
	private JComboBox<BasicIdentity> categoryComboBox;
	private JPanel sqlPreviewPanel;
	private JPanel generalPanel;
	private JPanel notificationsPanel;
	private JScrollPane alertScrollPane;
	private JPanel alertsPanel;
	private JPanel jobsPanel;

	private ModificationTracker<MsOperatorAlert> alertsModTracker;
	private ModificationTracker<MsOperatorJob> jobModTracker;

	public OperatorDialog() {
		this.setContentPane(contentPane);
		registerRules();
	}

	private void registerRules() {

	}

	public MsOperator getNewModel() {
		final MsOperator operator = model.operator.getOld();
		return new MsOperator(operator == null ? "" : operator.getId(),
				TextFieldGetter.INSTANCE.getTextOrCompute(nameTextField, () -> ""),
				CheckBoxGetter.INSTANCE.apply(enabledCheckBox),
				TextFieldGetter.INSTANCE.getTextOrCompute(emailTextField, () -> ""),
				ComboBoxGetter.INSTANCE.getText(categoryComboBox)
		);
	}

	private void bindTab1General() {
		String categoryName = null;
		final MsOperator operator = model.operator.getOld();
		if (operator != null) {
			nameTextField.setText(operator.getName());
			emailTextField.setText(operator.getEMail());
			enabledCheckBox.setSelected(operator.isEnabled());
			categoryName = operator.getCategoryName();
		}
		final List<BasicIdentity> categories = model.getOperatorCategories();
		JComboBoxUtilKt.synchronizeByName(categoryComboBox, categories, categoryName, null);
	}

	private void bindTab2Notifications() {
		final OperatorJobTableModel tableModel = new OperatorJobTableModel();
		tableModel.setRows(model.jobs);
		jobTable.setModel(tableModel);
		tableModel.initColumnSettings(jobTable);
		jobModTracker = new TableModificationTracker<>(tableModel);
		alertsModTracker = new CheckBoxListModTracker<>(alertScrollPane, model.getAlerts());
	}

	@Override
	public MsOperatorModel getModel() {
		model.operator.setNew(getNewModel());
		model.setAlertModifications(alertsModTracker.getModifications());
		model.setJobModifications(jobModTracker.getModifications());
		return model;
	}

	private MsOperatorModel model;

	@Override
	public void setModel(MsOperatorModel model) {
		this.model = model;
		final MsOperator operator = model.operator.getOld();
		if (operator != null) {
			isAlterMode = true;
			setTitle("Alter Operator " + operator.getName());
		} else {
			setTitle("Create Operator");
		}

		bindTab1General();
		bindTab2Notifications();
	}

	private boolean isAlterMode;

	@Override
	public boolean isAlterMode() {
		return isAlterMode;
	}

	@NotNull
	@Override
	public ScriptGeneratorBase<MsOperatorModel> getScriptGenerator() {
		return OperatorGenerator.INSTANCE;
	}

	@Override
	public void activateSqlPreview(@NotNull Function2<? super JPanel, ? super List<? extends JPanel>, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel, notificationsPanel));
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "OperatorDialog";
	}

	@Nullable
	@Override
	public String getModelHelpId() {
		return "ru.coding4fun.intellij.database.help.agent.operator";
	}
}
