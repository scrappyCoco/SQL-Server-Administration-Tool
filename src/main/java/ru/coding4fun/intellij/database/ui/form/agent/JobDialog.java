package ru.coding4fun.intellij.database.ui.form.agent;

import com.intellij.openapi.project.Project;
import com.intellij.ui.CheckBoxList;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.agent.JobGenerator;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.property.agent.MsNotifyLevel;
import ru.coding4fun.intellij.database.model.property.agent.job.*;
import ru.coding4fun.intellij.database.ui.DialogUtilsKt;
import ru.coding4fun.intellij.database.ui.JComboBoxUtilKt;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.UiDependencyManager;
import ru.coding4fun.intellij.database.ui.form.UiDependencyRule;
import ru.coding4fun.intellij.database.ui.form.common.CheckBoxListModTracker;
import ru.coding4fun.intellij.database.ui.form.common.ModificationTracker;
import ru.coding4fun.intellij.database.ui.form.state.CheckBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.ComboBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.StateChanger;
import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JobDialog extends JDialog implements ModelDialog<MsJobModel> {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JLabel nameLabel;
	private JTextField nameTextField;
	private JTextField ownerTextField;
	private JComboBox<BasicIdentity> categoryComboBox;
	private JTextArea descriptionTextArea;
	private JCheckBox enabledCheckBox;
	private JTextField createdTextField;
	private JTextField lastModifiedTextField;
	private JTextField lastExecutedTextField;
	private JLabel ownerLabel;
	private JLabel categoryTextField;
	private JLabel descriptionLabel;
	private JLabel lastModifiedLabel;
	private JLabel createdLabel;
	private JLabel lastExecutedLabel;
	private JTabbedPane tabbedPanel;
	private JPanel generalPanel;
	private JPanel stepsPanel;
	private JPanel schedulesPanel;
	private JPanel alertsPanel;
	private JPanel notificationsPanel;
	private JTable stepTable;
	private JComboBox<BasicIdentity> startStepComboBox;
	private JLabel stepLabel;
	private JButton upButton;
	private JButton downButton;
	private JButton addStepButton;
	private JButton deleteStepButton;
	private JTable scheduleTable;
	private JTable alertTable;
	private JCheckBox eMailCheckBox;
	private JCheckBox pageCheckBox;
	private JCheckBox eventCheckBox;
	private JCheckBox deleteJobCheckBox;
	private JComboBox<BasicIdentity> eMailComboBox;
	private JComboBox<BasicIdentity> eMailActionComboBox;
	private JComboBox<BasicIdentity> eventActionComboBox;
	private JComboBox<BasicIdentity> deleteActionComboBox;
	private JComboBox<BasicIdentity> stepDatabaseComboBox;
	private JTextArea commandTextArea;
	private JComboBox<BasicIdentity> subSystemComboBox;
	private CheckBoxList<MsSchedule> scheduleList;
	private JLabel databaseLabel;
	private JPanel sqlPreviewPanel;
	private JTextField retryAttemptsTextField;
	private JTextField retryIntervalTextField;
	private JTextField outputFileTextField;
	private JComboBox<BasicIdentity> outputFileComboBox;
	private JCheckBox logTableCheckBox;
	private JComboBox<BasicIdentity> logTableComboBox;
	private JCheckBox stepHistoryCheckBox;
	private JCheckBox jobHistoryCheckBox;
	private JCheckBox abortEventCheckBox;
	private JCheckBox useProxyCheckBox;
	private JComboBox proxyComboBox;
	private CheckBoxList<MsAlert> alertList;
	private JScrollPane scheduleScrollPane;
	private JScrollPane alertScrollPane;
	private ModificationTracker<MsJobStep> jobStepModTracker;
	private ModificationTracker<MsSchedule> scheduleModTracker;
	private ModificationTracker<MsAlert> alertModTracker;
	private Project project;
	private List<UiDependencyRule> uiRules;

	public JobDialog() {
		this.setContentPane(contentPane);
		registerRules();
	}

	private void registerRules() {
		//region Tab 5 - Notifications.
		UiDependencyRule eMailRule = new UiDependencyRule(eMailComboBox, eMailActionComboBox)
				.dependOn(eMailCheckBox)
				.must(eMailCheckBox::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule eventRule = new UiDependencyRule(eventActionComboBox)
				.dependOn(eventCheckBox)
				.must(eventCheckBox::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule deleteJobRule = new UiDependencyRule(deleteActionComboBox)
				.dependOn(deleteJobCheckBox)
				.must(deleteJobCheckBox::isSelected)
				.setStateChanger(StateChanger::enable);
		//endregion

		UiDependencyRule outputFileRule = new UiDependencyRule(outputFileComboBox)
				.dependOn(outputFileTextField)
				.must(() -> TextFieldGetter.INSTANCE.apply(outputFileTextField))
				.setStateChanger(StateChanger::enable);

		UiDependencyRule logTableRule = new UiDependencyRule(logTableComboBox)
				.dependOn(logTableCheckBox)
				.must(logTableCheckBox::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule proxyRules = new UiDependencyRule(proxyComboBox)
				.dependOn(useProxyCheckBox)
				.must(useProxyCheckBox::isSelected)
				.setStateChanger(StateChanger::enable);

		uiRules = UiDependencyManager.Companion.register(eMailRule, eventRule, deleteJobRule, outputFileRule,
				logTableRule, proxyRules);
	}

	private MsJobModel model;

	@NotNull
	@Contract(" -> new")
	private MsJobStep getSelectedStep() {
		return new MsJobStep("", "MyStepName",
				123,
				ComboBoxGetter.INSTANCE.getText(subSystemComboBox),
				(short) 0,
				(short) 0,
				TextFieldGetter.INSTANCE.getText(commandTextArea),
				ComboBoxGetter.INSTANCE.getText(stepDatabaseComboBox),
				ComboBoxGetter.INSTANCE.getText(proxyComboBox),
				0,//TextFieldGetter.INSTANCE.getInt(retryAttemptsTextField),
				0,//TextFieldGetter.INSTANCE.getInt(retryIntervalTextField),
				"",
				false,
				false,
				CheckBoxGetter.INSTANCE.apply(stepHistoryCheckBox),
				CheckBoxGetter.INSTANCE.apply(jobHistoryCheckBox),
				CheckBoxGetter.INSTANCE.apply(abortEventCheckBox),
				false,
				false);
	}

	private Unit updateSelectedStep(@NotNull final MsJobStep step) {
		//1JComboBoxUtilKt.synchronizeByName(subSystemComboBox, dataProvider.getSubSystems(), step.getType(), null);
		commandTextArea.setText(step.getCommand());

		if ("TSQL".equals(step.getType())) {
			databaseLabel.setEnabled(true);
			//JComboBoxUtilKt.synchronizeByName(stepDatabaseComboBox, dataProvider.getDatabases(), step.getDbName(), null);
		} else {
			databaseLabel.setEnabled(false);
			stepDatabaseComboBox.setEnabled(false);
			stepDatabaseComboBox.removeAllItems();
		}

		JComboBoxUtilKt.synchronizeByName(proxyComboBox, Collections.EMPTY_LIST, step.getProxyName(), useProxyCheckBox);
		retryAttemptsTextField.setText(Integer.toString(step.getRetryAttempts()));
		retryIntervalTextField.setText(Integer.toString(step.getRetryInterval()));
		outputFileTextField.setText(step.getOutputFile());
		//logTableCheckBox
		stepHistoryCheckBox.setSelected(step.getStepHistory());
		jobHistoryCheckBox.setSelected(step.getJobHistory());
		abortEventCheckBox.setSelected(step.getAbortEvent());

		String selectedOutputFile = "-1";
		if (step.getAppendFile()) {
			selectedOutputFile = Bits.AppendFile.id;
		} else if (step.getOverrideFile()) {
			selectedOutputFile = Bits.OverrideFile.id;
		}

		String selectedLogTableFile = "-1";
		if (step.getAppendTable()) {
			selectedLogTableFile = Bits.AppendTable.id;
		} else if (step.getOverrideTable()) {
			selectedLogTableFile = Bits.OverrideTable.id;
		}

		JComboBoxUtilKt.synchronizeById(outputFileComboBox, getOutputFilesOptions(), selectedOutputFile, null);
		JComboBoxUtilKt.synchronizeById(logTableComboBox, getLogTableOptions(), selectedLogTableFile, logTableCheckBox);

		return Unit.INSTANCE;
	}

	private void bindTab2Steps() {
		DialogUtilsKt.disableAll(stepsPanel);
//		final List<MsJobStep> jobSteps = model.steps;
//		jobStepModificationTracker = new JobStepMediator(stepTable, jobSteps, this::updateSelectedStep, this::getSelectedStep,
//				subSystemComboBox, stepDatabaseComboBox, commandTextArea, useProxyCheckBox, proxyComboBox,
//				retryAttemptsTextField, retryIntervalTextField, outputFileTextField, outputFileComboBox,
//				logTableCheckBox, logTableComboBox, stepHistoryCheckBox, jobHistoryCheckBox, abortEventCheckBox);
//
//		final List<BasicIdentity> simpleJobSteps = jobSteps.stream()
//				.map(it -> new BasicIdentity(it.getId(), it.getName()))
//				.collect(Collectors.toList());
//
//		final String startStep = (oldModel == null) ? null : oldModel.getStartStepId();
//		JComboBoxUtilKt.synchronizeById(startStepComboBox, simpleJobSteps, startStep, null);
//		JComboBoxUtilKt.synchronize(outputFileComboBox, getOutputFilesOptions(), null, null);
//		JComboBoxUtilKt.synchronize(logTableComboBox, getLogTableOptions(), logTableCheckBox, null);
	}

	private void bindTab3Schedule() {
		scheduleModTracker = new CheckBoxListModTracker<>(scheduleScrollPane, model.schedules);
	}

//	private void bindTab5Notification(@NotNull MsJobComposite composite) {
//		final List<BasicIdentity> jobOperators = composite.operaotrs;
//
//		final String eMailOperatorId = oldModel == null ? null : oldModel.getEMailOperatorId();
//		JComboBoxUtilKt.synchronizeById(eMailComboBox, jobOperators, eMailOperatorId, eMailCheckBox);
//
//		final List<BasicIdentity> notifyNames = Arrays.stream(MsNotifyLevel.values())
//				.map(level -> new BasicIdentity(Byte.toString(level.getId()), level.getWhenDescription()))
//				.collect(Collectors.toList());
//
//		String emailNotifyLevel = oldModel == null || oldModel.getEMailNotifyLevel() == null
//				? null
//				: Byte.toString(oldModel.getEMailNotifyLevel().getId());
//
//		String eventNotifyLevel = oldModel == null || oldModel.getEventLogLevel() == null
//				? null
//				: Byte.toString(oldModel.getEventLogLevel().getId());
//
//		String deleteLevel = oldModel == null || oldModel.getDeleteLevel() == null
//				? null
//				: Byte.toString(oldModel.getDeleteLevel().getId());
//
//		JComboBoxUtilKt.synchronizeById(eMailActionComboBox, notifyNames, emailNotifyLevel, eMailCheckBox);
//		JComboBoxUtilKt.synchronizeById(eventActionComboBox, notifyNames, eventNotifyLevel, eventCheckBox);
//		JComboBoxUtilKt.synchronizeById(deleteActionComboBox, notifyNames, deleteLevel, deleteJobCheckBox);
//	}

	@NotNull
	private List<BasicIdentity> getOutputFilesOptions() {
		return List.of(Bits.OverrideFile, Bits.AppendFile)
				.stream().map(b -> new BasicIdentity(b.id, b.title)).collect(Collectors.toUnmodifiableList());
	}

	@NotNull
	private List<BasicIdentity> getLogTableOptions() {
		return List.of(Bits.OverrideTable, Bits.AppendTable)
				.stream().map(b -> new BasicIdentity(b.id, b.title)).collect(Collectors.toUnmodifiableList());
	}

	private @Nullable
	MsNotifyLevel getSelectedLevel(@NotNull JComboBox<BasicIdentity> comboBox) {
		MsNotifyLevel notifyLevel = null;
		final String selectedLevelId = ComboBoxGetter.INSTANCE.getSelected(comboBox, BasicIdentity::getId);
		if (selectedLevelId != null) {
			notifyLevel = Arrays.stream(MsNotifyLevel.values())
					.filter(l -> selectedLevelId.equals(Byte.toString(l.getId())))
					.findFirst()
					.get();
		}
		return notifyLevel;
	}


	public MsJob getNewModel() {
		return new MsJob("",
				TextFieldGetter.INSTANCE.getTextOrCompute(nameTextField, () -> "My job name must be specified"),
				CheckBoxGetter.INSTANCE.apply(enabledCheckBox),
				TextFieldGetter.INSTANCE.getText(descriptionTextArea),
				ComboBoxGetter.INSTANCE.getSelected(startStepComboBox, BasicIdentity::getId),
				ComboBoxGetter.INSTANCE.getSelected(categoryComboBox, BasicIdentity::getId),
				ComboBoxGetter.INSTANCE.getSelected(categoryComboBox, BasicIdentity::getName),
				TextFieldGetter.INSTANCE.getText(ownerTextField),
				null,
				null,
				null,
				getSelectedLevel(eMailActionComboBox),
				getSelectedLevel(eventActionComboBox),
				ComboBoxGetter.INSTANCE.getSelected(eMailComboBox, BasicIdentity::getId),
				ComboBoxGetter.INSTANCE.getSelected(eMailComboBox, BasicIdentity::getName),
				getSelectedLevel(deleteActionComboBox)
		);
	}

	private void bindTab4Alert() {
		alertModTracker = new CheckBoxListModTracker<>(alertScrollPane, model.alerts);
	}

	@Nullable

	public MsJobParameter getModelParam() {
		return new MsJobParameter(
				alertModTracker.getModifications(),
				scheduleModTracker.getModifications(),
				jobStepModTracker.getModifications()
		);
	}

	@Override
	public MsJobModel getModel() {
		model.job.setNew(getNewModel());
		model.setScheduleMods(scheduleModTracker.getModifications());
		model.setAlertMods(alertModTracker.getModifications());
		return model;
	}

	@Override
	public void setModel(MsJobModel model) {
		this.model = model;
		final MsJob job = model.job.getOld();
		String currentCategory;
		if (job != null) {
			isAlterMode = true;
			nameTextField.setText(job.getName());
			ownerTextField.setText(job.getOwnerName());
			descriptionTextArea.setText(job.getDescription());
			enabledCheckBox.setSelected(job.isEnabled());
			createdTextField.setText(job.getDateCreated());
			lastModifiedTextField.setText(job.getLastModified());
			lastExecutedTextField.setText(job.getLastExecuted());
			currentCategory = job.getCategoryName();

			setTitle("Alter Job " + job.getName());
		} else {
			currentCategory = null;
			setTitle("Create Job");
		}

		final List<BasicIdentity> jobCategories = model.categories;
		JComboBoxUtilKt.synchronizeByName(categoryComboBox, jobCategories, currentCategory, null);

		bindTab2Steps();
		bindTab3Schedule();
		bindTab4Alert();
//		bindTab5Notification(model);

		for (UiDependencyRule uiRule : uiRules) {
			uiRule.apply();
		}
	}

	private boolean isAlterMode;
	@Override
	public boolean isAlterMode() {
		return isAlterMode;
	}

	@NotNull
	@Override
	public ScriptGeneratorBase<MsJobModel> getScriptGenerator() {
		return JobGenerator.INSTANCE;
	}

	@Override
	public void activateSqlPreview(@NotNull Function2<? super JPanel, ? super List<? extends JPanel>, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel, stepsPanel, schedulesPanel, alertsPanel, notificationsPanel));
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "JobDialog";
	}

	private enum Bits {
		OverrideFile("0", "Overwrite output file"),
		AppendFile("2", "Append to output file"),
		StepHistory("4", null),
		JobHistory("32", null),
		AbortEvent("64", null),
		OverrideTable("8", "Overwrite existing history"),
		AppendTable("16", "Append to existing history");


		public String id;
		public String title;

		Bits(String id, String title) {
			this.id = id;
			this.title = title;
		}
	}
}
