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

import com.intellij.ui.CheckBoxList;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.intellij.database.data.property.DbUtils;
import ru.coding4fun.intellij.database.data.property.agent.PerformanceCounterManager;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.agent.AlertGenerator;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.property.agent.alert.*;
import ru.coding4fun.intellij.database.ui.JComboBoxUtilKt;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.MsSqlScriptState;
import ru.coding4fun.intellij.database.ui.form.UiDependencyManager;
import ru.coding4fun.intellij.database.ui.form.UiDependencyRule;
import ru.coding4fun.intellij.database.ui.form.common.CheckBoxListModTracker;
import ru.coding4fun.intellij.database.ui.form.common.ModelModification;
import ru.coding4fun.intellij.database.ui.form.common.ModificationTracker;
import ru.coding4fun.intellij.database.ui.form.state.CheckBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.ComboBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.StateChanger;
import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AlertDialog extends JDialog implements ModelDialog<MsAlertModel> {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTabbedPane tabbedPane1;
	private JTextField nameTextField;
	private JCheckBox enableCheckBox;
	private JComboBox<BasicIdentity> typeComboBox;
	private JCheckBox databaseCheckBox;
	private JComboBox<BasicIdentity> databaseComboBox;
	private JRadioButton errorNumberRadioButton;
	private JRadioButton severityRadioButton;
	private JTextField errorNumberTextField;
	private JComboBox<BasicIdentity> severityComboBox;
	private JTextField messageTextField;
	private JComboBox<String> performanceObjectComboBox;
	private JComboBox<String> performanceCounterComboBox;
	private JComboBox<String> performanceInstanceComboBox;
	private JComboBox<BasicIdentity> signComboBox;
	private JTextField valueTextField;
	private JTextField wmiNamespaceTextField;
	private JTextArea wmiQueryTextArea;
	private JCheckBox executeJobCheckBox;
	private JComboBox<BasicIdentity> jobComboBox;
	private JCheckBox includeAlertErrorTextCheckBox;
	private JTextArea notificationMessageTextArea;
	private JTextField minutesTextField;
	private JTextField secondsTextField;
	private JPanel eventPanel;
	private JPanel performanceRule;
	private JPanel wmiPanel;
	private CheckBoxList<Operator> operatorList;
	private JScrollPane operatorScrollPane;
	private JPanel sqlPreviewPanel;
	private JPanel generalPanel;
	private JPanel responsePanel;
	private JPanel optionsPanel;
	private JComboBox<BasicIdentity> categoryComboBox;
	private JCheckBox categoryCheckBox;
	private PerformanceCounterManager performanceCounterManager;
	private Map<String, String> signDescriptions;
	private List<BasicIdentity> signs;
	private ModificationTracker<Operator> operatorModTracker;

	public AlertDialog() {
		this.setContentPane(contentPane);

		final TreeMap<String, String> captions = new TreeMap<>();
		captions.put("<", "Falls below");
		captions.put("=", "Becomes equal to");
		captions.put(">", "Rises above");
		signDescriptions = Collections.unmodifiableNavigableMap(captions);
		signs = List.of(
				new BasicIdentity("<", "Falls below"),
				new BasicIdentity("=", "Becomes equal to"),
				new BasicIdentity(">", "Rises above")
		);

		registerRules();
	}

	@SuppressWarnings("DuplicatedCode")
	private void registerRules() {
		UiDependencyRule eventPanelRule = new UiDependencyRule(eventPanel)
				.dependOn(typeComboBox)
				.must(this::eventTypeSelected)
				.setStateChanger(StateChanger::visibleRecurse);

		UiDependencyRule performancePanelRule = new UiDependencyRule(performanceRule)
				.dependOn(typeComboBox)
				.must(this::performanceTypeSelected)
				.setStateChanger(StateChanger::visibleRecurse);

		UiDependencyRule wmiRule = new UiDependencyRule(wmiPanel)
				.dependOn(typeComboBox)
				.must(this::wmiTypeSelected)
				.setStateChanger(StateChanger::visibleRecurse);

		UiDependencyRule executeJobRule = new UiDependencyRule(jobComboBox)
				.dependOn(executeJobCheckBox)
				.must(executeJobCheckBox::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule errorNumberRule = new UiDependencyRule(errorNumberTextField)
				.dependOn(errorNumberRadioButton, severityRadioButton)
				.must(errorNumberRadioButton::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule severityRule = new UiDependencyRule(severityComboBox)
				.dependOn(errorNumberRadioButton, severityRadioButton)
				.must(severityRadioButton::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule databaseNameRule = new UiDependencyRule(databaseComboBox)
				.dependOn(databaseCheckBox)
				.must(() -> CheckBoxGetter.INSTANCE.apply(databaseCheckBox))
				.setStateChanger(StateChanger::enable);

		final List<UiDependencyRule> rules = UiDependencyManager.Companion.register(eventPanelRule, performancePanelRule, wmiRule, executeJobRule,
				errorNumberRule, severityRule, databaseNameRule);

		for (UiDependencyRule rule : rules) rule.apply();
	}

	private boolean eventTypeSelected() {
		return isSelected(AlertType.SqlEvent);
	}

	private boolean performanceTypeSelected() {
		return isSelected(AlertType.SqlPerformance);
	}

	private boolean wmiTypeSelected() {
		return isSelected(AlertType.WmiEvent);
	}

	private AlertType getAlertType() {
		if (eventTypeSelected()) return AlertType.SqlEvent;
		if (performanceTypeSelected()) return AlertType.SqlPerformance;
		return AlertType.WmiEvent;
	}

	private boolean isSelected(@NotNull AlertType alertType) {
		if (typeComboBox.getSelectedItem() == null) return false;
		return alertType.getId().equals(((BasicIdentity) typeComboBox.getSelectedItem()).getId());
	}

	private void bindTab1General(@NotNull MsAlertModel model) {
		MsAlert oldModel = model.getAlert().getOld();
		nameTextField.setText(oldModel.getName());
		enableCheckBox.setSelected(oldModel.isEnabled());
		final List<BasicIdentity> alertTypes = AlertTypeUtilsKt.getIdentityList();
		JComboBoxUtilKt.synchronizeById(typeComboBox, alertTypes, oldModel.getType().getId(), null);
		JComboBoxUtilKt.synchronizeByName(databaseComboBox, model.databases, oldModel.getDatabaseName(), databaseCheckBox);
		JComboBoxUtilKt.synchronizeByName(categoryComboBox, model.categories, oldModel.getCategoryName(), categoryCheckBox);

		final boolean isNotBlankErrorNumber = oldModel.getErrorNumber() != null && !oldModel.getErrorNumber().isBlank();
		errorNumberRadioButton.setSelected(isNotBlankErrorNumber);
		JComboBoxUtilKt.synchronizeById(severityComboBox, Severity.INSTANCE.getTypes(), oldModel.getSeverity(), null);
		if (oldModel.getSeverity() != null) {
			severityRadioButton.setSelected(true);
		}
		messageTextField.setText(oldModel.getNotificationMessage());

		//region WMI
		wmiNamespaceTextField.setText(oldModel.getWmiNamespace());
		wmiQueryTextArea.setText(oldModel.getWmiQuery());
		//endregion

		//region Performance counters
		performanceCounterManager = model.perfCounterManager;
		performanceObjectComboBox.addItemListener(e -> synchronizePerf(PerfLevel.Counter));
		performanceCounterComboBox.addItemListener(e -> synchronizePerf(PerfLevel.Instance));

		if (oldModel.getPerformanceObject() == null) {
			synchronizePerf(PerfLevel.Object);
			JComboBoxUtilKt.initialize(signComboBox, signs);
		} else {
			final List<String> performanceObjects = performanceCounterManager.getChildren();
			JComboBoxUtilKt.synchronizeByString(performanceObjectComboBox, performanceObjects, oldModel.getPerformanceObject(), null);

			final List<String> performanceCounters = performanceCounterManager.getChildren(oldModel.getPerformanceObject());
			JComboBoxUtilKt.synchronizeByString(performanceCounterComboBox, performanceCounters, oldModel.getPerformanceCounter(), null);

			final List<String> performanceInstances = performanceCounterManager.getChildren(
					oldModel.getPerformanceObject(),
					oldModel.getPerformanceCounter()
			);
			JComboBoxUtilKt.synchronizeByString(performanceInstanceComboBox, performanceInstances, oldModel.getPerformanceInstance(), null);
			final String signDescription = signDescriptions.get(oldModel.getPerformanceSign());
			JComboBoxUtilKt.synchronizeById(signComboBox, signs, signDescription, null);
			if (oldModel.getPerformanceValue() != null) {
				valueTextField.setText(oldModel.getPerformanceValue().toString());
			}
		}
		//endregion
	}

	private void bindTab2Response(@NotNull MsAlertModel model) {
		MsAlert oldModel = model.getAlert().getOld();
		JComboBoxUtilKt.synchronizeById(jobComboBox, model.getJobs(), oldModel.getJobId(), executeJobCheckBox);
		List<Operator> operators = model.operators.stream().map(ModelModification<Operator>::getOld).collect(Collectors.toList());
		operatorModTracker = new CheckBoxListModTracker<>(operatorScrollPane, operators);
	}

	private void bindTab3Options() {
		MsAlert oldModel = model.getAlert().getOld();
		if (oldModel != null) {
			if (oldModel.getMinutes() != null) minutesTextField.setText(oldModel.getMinutes().toString());
			if (oldModel.getSeconds() != null) minutesTextField.setText(oldModel.getSeconds().toString());
			if (oldModel.getNotificationMessage() != null)
				notificationMessageTextArea.setText(oldModel.getNotificationMessage());
		}
	}

	private void synchronizePerf(@NotNull PerfLevel perfLevel) {
		switch (perfLevel) {
			case Object:
				synchronizePerf(performanceObjectComboBox);
				break;
			case Counter:
				synchronizePerf(performanceCounterComboBox, getSelectedPerfObject());
				break;
			case Instance:
				synchronizePerf(performanceInstanceComboBox, getSelectedPerfObject(), getSelectedPerfCounter());
				break;
		}
	}

	private String getSelectedPerfObject() {
		return getSelectedValue(performanceObjectComboBox);
	}

	private String getSelectedPerfCounter() {
		return getSelectedValue(performanceCounterComboBox);
	}

	private void synchronizePerf(JComboBox<String> comboBox, String... pathParts) {
		final List<String> targetValues = performanceCounterManager.getChildren(pathParts);
		JComboBoxUtilKt.initialize(comboBox, targetValues);
		if (targetValues.size() > 0) {
			comboBox.setSelectedIndex(0);
			comboBox.setEnabled(true);
		} else {
			comboBox.setEnabled(false);
		}
	}

	@Nullable
	private String getSelectedValue(@NotNull JComboBox<String> comboBox) {
		if (comboBox.getSelectedItem() == null) return null;
		return comboBox.getSelectedItem().toString();
	}

	private @Nullable String getPerformanceCondition() {
		StringBuilder stringBuilder = new StringBuilder();

		class Component {
			public final Function0<String> valueGetter;
			public final Boolean isRequired;
			public Component(Function0<String> valueGetter, Boolean isRequired) {
				this.valueGetter = valueGetter;
				this.isRequired = isRequired;
			}
		}

		var components = List.of(
				new Component(() -> ComboBoxGetter.INSTANCE.getText(performanceObjectComboBox), true),
				new Component(() -> ComboBoxGetter.INSTANCE.getText(performanceCounterComboBox), true),
				new Component(() -> ComboBoxGetter.INSTANCE.getText(performanceInstanceComboBox), false),
				new Component(() -> ComboBoxGetter.INSTANCE.getSelected(signComboBox, BasicIdentity::getId), true),
				new Component(() -> TextFieldGetter.INSTANCE.getText(valueTextField), true)
		);

		for (Component component : components) {
			String value = component.valueGetter.invoke();
			if (value == null || value.isBlank()) {
				if (component.isRequired) return null;
			} else {
				if (stringBuilder.length() > 0) stringBuilder.append("|");
				stringBuilder.append(value);
			}
		}

		return stringBuilder.toString();
	}

	private MsAlertModel model;
	@Override
	public MsAlertModel getModel() {
		MsAlert alertOld = model.alert.getOld();

		model.alert.setNew(new MsAlert(
				alertOld.getId(),
				TextFieldGetter.INSTANCE.getTextOrCompute(nameTextField, () -> "My alert"),
				ComboBoxGetter.INSTANCE.getSelected(categoryComboBox, BasicIdentity::getName),
				enableCheckBox.isSelected(),
				getAlertType(),
				ComboBoxGetter.INSTANCE.getSelected(databaseComboBox, BasicIdentity::getName),
				TextFieldGetter.INSTANCE.getText(errorNumberTextField),
				ComboBoxGetter.INSTANCE.getSelected(severityComboBox, BasicIdentity::getId),
				TextFieldGetter.INSTANCE.getText(messageTextField),
				TextFieldGetter.INSTANCE.getText(wmiNamespaceTextField),
				TextFieldGetter.INSTANCE.getText(wmiQueryTextArea),
				ComboBoxGetter.INSTANCE.getSelected(jobComboBox, BasicIdentity::getId),
				ComboBoxGetter.INSTANCE.getSelected(jobComboBox, BasicIdentity::getName),
				includeAlertErrorTextCheckBox.isSelected(),
				TextFieldGetter.INSTANCE.getText(notificationMessageTextArea),
				TextFieldGetter.INSTANCE.getIntOrCompute(minutesTextField, () -> 0),
				TextFieldGetter.INSTANCE.getIntOrCompute(secondsTextField, () -> 0),
				getPerformanceCondition())
		);

		model.operators = operatorModTracker.getModifications();

		return model;
	}

	@Override
	public void setModel(MsAlertModel model) {
		this.model = model;
		final MsAlert alert = model.getAlert().getOld();
		isAlterMode = !DbUtils.defaultId.equals(alert.getId());
		if (isAlterMode) {
			setTitle("Alert Alert " + model.alert.getOld().getName());
		} else {
			setTitle("Create Alert");
		}

		bindTab1General(model);
		bindTab2Response(model);
		bindTab3Options();
	}

	private boolean isAlterMode;
	@Override
	public boolean isAlterMode() {
		return isAlterMode;
	}

	@NotNull
	@Override
	public ScriptGeneratorBase<MsAlertModel> getScriptGenerator() {
		return AlertGenerator.INSTANCE;
	}

	@Override
	public void activateSqlPreview(@NotNull Function3<? super JPanel, ? super List<? extends JPanel>, ? super MsSqlScriptState, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel, responsePanel, optionsPanel), null);
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "AlertDialog";
	}

	@Nullable
	@Override
	public String getModelHelpId() {
		return "ru.coding4fun.intellij.database.help.agent.alert";
	}

	private enum PerfLevel {
		Object,
		Counter,
		Instance
	}
}
