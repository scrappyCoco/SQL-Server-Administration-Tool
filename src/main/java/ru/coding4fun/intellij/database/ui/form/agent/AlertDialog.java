package ru.coding4fun.intellij.database.ui.form.agent;

import com.intellij.ui.CheckBoxList;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.intellij.database.data.property.agent.PerformanceCounterManager;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.agent.AlertGenerator;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.property.agent.alert.*;
import ru.coding4fun.intellij.database.ui.JComboBoxUtilKt;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.UiDependencyManager;
import ru.coding4fun.intellij.database.ui.form.UiDependencyRule;
import ru.coding4fun.intellij.database.ui.form.common.CheckBoxListModTracker;
import ru.coding4fun.intellij.database.ui.form.common.ModificationTracker;
import ru.coding4fun.intellij.database.ui.form.state.CheckBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.StateChanger;

import javax.swing.*;
import java.util.*;
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
	private JTextField notificationMessageTextField;
	private JComboBox<String> performanceObjectComboBox;
	private JComboBox<String> performanceCounterComboBox;
	private JComboBox<String> performanceInstanceComboBox;
	private JComboBox<String> signComboBox;
	private JTextField valueTextField;
	private JTextField wmiNamespaceTextField;
	private JTextArea wmiQueryTextArea;
	private JCheckBox executeJobCheckBox;
	private JComboBox<BasicIdentity> jobComboBox;
	private JCheckBox includeAlertErrorTextCheckBox;
	private JTextArea addNotificationMessageTextArea;
	private JTextField minutesTextField;
	private JTextField a0TextField;
	private JPanel eventPanel;
	private JPanel performanceRule;
	private JPanel wmiPanel;
	private CheckBoxList<Operator> operatorList;
	private JScrollPane operatorScrollPane;
	private MsAlert oldModel;
	private PerformanceCounterManager performanceCounterManager;
	private Map<String, String> signDescriptions;
	private List<String> signs;
	private ModificationTracker<Operator> operatorModTracker;

	public AlertDialog() {
		this.setContentPane(contentPane);

		final TreeMap<String, String> captions = new TreeMap<>();
		captions.put("<", "Falls below");
		captions.put("=", "Becomes equal to");
		captions.put(">", "Rises above");
		signDescriptions = Collections.unmodifiableNavigableMap(captions);
		signs = List.of(captions.values().toArray(new String[0]));

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

	private boolean isSelected(@NotNull AlertType alertType) {
		if (typeComboBox.getSelectedItem() == null) return false;
		return alertType.getId().equals(((BasicIdentity) typeComboBox.getSelectedItem()).getId());
	}

	@Nullable
	public MsAlert getOldModel() {
		return oldModel;
	}

	public MsAlert getNewModel() {
		return null;
	}

//	@Override
//	public void bindData(AlertDataProvider alertDataProvider, @Nullable String modelId) {
//		if (modelId != null) {
//			oldModel = alertDataProvider.getModel(modelId);
//			bindTab1General(alertDataProvider);
//			bindTab2Response(alertDataProvider, modelId);
//			bindTab3Options();
//			setTitle("Alert Alert " + oldModel.getName());
//		} else {
//			setTitle("Create Alert");
//		}
//	}

	private void bindTab1General(@NotNull MsAlertModel model) {
		nameTextField.setText(oldModel.getName());
		enableCheckBox.setSelected(oldModel.isEnabled());
		final List<BasicIdentity> types = Arrays.stream(AlertType.values())
				.map(type -> new BasicIdentity(type.getId(), type.getTitle()))
				.collect(Collectors.toList());
		JComboBoxUtilKt.synchronizeById(typeComboBox, types, oldModel.getType().getId(), null);

		JComboBoxUtilKt.synchronizeByName(databaseComboBox, model.databases, oldModel.getDatabaseName(), databaseCheckBox);

		final boolean isNotBlankErrorNumber = oldModel.getErrorNumber() != null && !oldModel.getErrorNumber().isBlank();
		errorNumberRadioButton.setSelected(isNotBlankErrorNumber);
		if (oldModel.getSeverity() != null) {
			JComboBoxUtilKt.synchronizeById(severityComboBox, Severity.INSTANCE.getTypes(), oldModel.getSeverity(), null);
			severityRadioButton.setSelected(true);
		}
		notificationMessageTextField.setText(oldModel.getNotificationMessage());

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
			JComboBoxUtilKt.synchronizeByString(signComboBox, signs, signDescription, null);
			if (oldModel.getPerformanceValue() != null) {
				valueTextField.setText(oldModel.getPerformanceValue().toString());
			}
		}
		//endregion
	}

	private void bindTab2Response(@NotNull MsAlertModel model) {
		if (oldModel.getJobId() != null) {
			JComboBoxUtilKt.synchronizeById(jobComboBox, model.getJobs(), oldModel.getJobId(), executeJobCheckBox);
		} else {
			JComboBoxUtilKt.initialize(jobComboBox, model.getJobs());
		}

		operatorModTracker = new CheckBoxListModTracker<>(operatorScrollPane, model.operators);
	}

	private void bindTab3Options() {
		if (oldModel != null) {
			if (oldModel.getMinutes() != null) minutesTextField.setText(oldModel.getMinutes().toString());
			if (oldModel.getSeconds() != null) minutesTextField.setText(oldModel.getSeconds().toString());
			if (oldModel.getNotificationMessage() != null)
				addNotificationMessageTextArea.setText(oldModel.getNotificationMessage());
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

	private MsAlertModel model;
	@Override
	public MsAlertModel getModel() {
		return model;
	}

	@Override
	public void setModel(MsAlertModel model) {
		this.model = model;
		final MsAlert alert = model.getAlert().getOld();
		if (alert != null) {
			isAlterMode = true;
			bindTab1General(model);
			bindTab2Response(model);
			bindTab3Options();
			setTitle("Alert Alert " + oldModel.getName());
		} else {
			setTitle("Create Alert");
		}
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
	public void activateSqlPreview(@NotNull Function2<? super JPanel, ? super List<? extends JPanel>, Unit> activateFun) {

	}

	@NotNull
	@Override
	public String getDialogId() {
		return "AlertDialog";
	}

	private enum PerfLevel {
		Object,
		Counter,
		Instance
	}
}
