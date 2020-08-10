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

import com.intellij.openapi.project.Project;
import com.intellij.ui.CheckBoxList;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.intellij.database.data.property.DbUtils;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.agent.JobGenerator;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.property.agent.MsNotifyLevel;
import ru.coding4fun.intellij.database.model.property.agent.MsNotifyLevels;
import ru.coding4fun.intellij.database.model.property.agent.job.MsAlert;
import ru.coding4fun.intellij.database.model.property.agent.job.MsJob;
import ru.coding4fun.intellij.database.model.property.agent.job.MsJobModel;
import ru.coding4fun.intellij.database.model.property.agent.job.MsSchedule;
import ru.coding4fun.intellij.database.ui.JComboBoxUtilKt;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.MsSqlScriptState;
import ru.coding4fun.intellij.database.ui.form.UiDependencyManager;
import ru.coding4fun.intellij.database.ui.form.UiDependencyRule;
import ru.coding4fun.intellij.database.ui.form.agent.job.JobStepMediator;
import ru.coding4fun.intellij.database.ui.form.common.CheckBoxListModTracker;
import ru.coding4fun.intellij.database.ui.form.common.ModificationTracker;
import ru.coding4fun.intellij.database.ui.form.state.CheckBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.ComboBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.StateChanger;
import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
	private JComboBox<BasicIdentity> proxyComboBox;
	private CheckBoxList<MsAlert> alertList;
	private JScrollPane scheduleScrollPane;
	private JScrollPane alertScrollPane;
	private JPanel stepPanel;
	private JButton restoreButton;
	private JobStepMediator jobStepMediator;
	private ModificationTracker<MsSchedule> scheduleModTracker;
	private ModificationTracker<MsAlert> alertModTracker;
	private Project project;
	private List<UiDependencyRule> uiRules;
	private MsSqlScriptState scriptState;


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

	public JobDialog() {
		this.setContentPane(contentPane);

		stepTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		stepTable.getSelectionModel().addListSelectionListener(listSelectionEvent -> {
			downButton.setEnabled(stepTable.getSelectedRow() < jobStepMediator.getTableModel().getRows().size() - 1);
			upButton.setEnabled(stepTable.getSelectedRow() > 0);
		});

		registerRules();

		addStepButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				jobStepMediator.addStep();
			}
		});

		deleteStepButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				jobStepMediator.deleteStep();
			}
		});

		upButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				jobStepMediator.upStep();
			}
		});

		downButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				jobStepMediator.downStep();
			}
		});

		restoreButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				jobStepMediator.restoreStep();
			}
		});

		scriptState = new MsSqlScriptState();
	}

	private void bindTab2Steps() {
		jobStepMediator = new JobStepMediator(model, scriptState, stepTable, stepDatabaseComboBox,
				commandTextArea, useProxyCheckBox, proxyComboBox, retryAttemptsTextField, retryIntervalTextField,
				outputFileTextField, outputFileComboBox, logTableCheckBox, logTableComboBox, stepHistoryCheckBox,
				jobHistoryCheckBox, abortEventCheckBox, stepPanel, startStepComboBox);

		jobStepMediator.getTableModel().addTableModelListener(tableModelEvent -> updateButtons());

		updateButtons();
	}

	private void bindTab3Schedule() {
		scheduleModTracker = new CheckBoxListModTracker<>(scheduleScrollPane, model.schedules);
	}

	private void updateButtons() {
		boolean hasAnySteps = jobStepMediator.getModifications().size() > 0;
		upButton.setEnabled(hasAnySteps);
		downButton.setEnabled(hasAnySteps);
		deleteStepButton.setEnabled(hasAnySteps);
		restoreButton.setEnabled(hasAnySteps);
	}

	private void bindTab5Notification(@NotNull MsJobModel model) {
		final List<BasicIdentity> jobOperators = model.operators;

		final MsJob oldJob = model.job.getOld();

		final String eMailOperatorId = oldJob.getEMailOperatorId();
		JComboBoxUtilKt.synchronizeById(eMailComboBox, jobOperators, eMailOperatorId, eMailCheckBox);

		final List<BasicIdentity> notifyNames = MsNotifyLevels.INSTANCE.getIdList();

		String emailNotifyLevel = oldJob.getEMailNotifyLevel() == null ? null : oldJob.getEMailNotifyLevel().getId();
		String eventNotifyLevel = oldJob.getEventLogLevel() == null ? null : oldJob.getEventLogLevel().getId();
		String deleteLevel = oldJob.getDeleteLevel() == null ? null : oldJob.getDeleteLevel().getId();

		JComboBoxUtilKt.synchronizeById(eMailActionComboBox, notifyNames, emailNotifyLevel, eMailCheckBox);
		JComboBoxUtilKt.synchronizeById(eventActionComboBox, notifyNames, eventNotifyLevel, eventCheckBox);
		JComboBoxUtilKt.synchronizeById(deleteActionComboBox, notifyNames, deleteLevel, deleteJobCheckBox);
	}

	private void bindTab4Alert() {
		alertModTracker = new CheckBoxListModTracker<>(alertScrollPane, model.alerts);
	}

	public MsJob getNewModel() {
		final MsJob jobOld = model.job.getOld();
		final String stepNumber = ComboBoxGetter.INSTANCE.getSelected(startStepComboBox, BasicIdentity::getId);
		//final String stepNumber = selectedStepId == null ? null : jobStepMediator.getStepNumber(selectedStepId);

		return new MsJob(jobOld.getId(),
				TextFieldGetter.INSTANCE.getTextOrCompute(nameTextField, () -> "Job name must be specified"),
				CheckBoxGetter.INSTANCE.apply(enabledCheckBox),
				TextFieldGetter.INSTANCE.getText(descriptionTextArea),
				stepNumber,
				ComboBoxGetter.INSTANCE.getSelected(categoryComboBox, BasicIdentity::getId),
				ComboBoxGetter.INSTANCE.getSelected(categoryComboBox, BasicIdentity::getName),
				TextFieldGetter.INSTANCE.getText(ownerTextField),
				createdTextField.getText(),
				lastModifiedTextField.getText(),
				lastExecutedTextField.getText(),
				getSelectedLevel(eMailActionComboBox),
				getSelectedLevel(eventActionComboBox),
				ComboBoxGetter.INSTANCE.getSelected(eMailComboBox, BasicIdentity::getId),
				ComboBoxGetter.INSTANCE.getSelected(eMailComboBox, BasicIdentity::getName),
				getSelectedLevel(deleteActionComboBox)
		);
	}

	@Override
	public MsJobModel getModel() {
		model.job.setNew(getNewModel());
		model.setScheduleMods(scheduleModTracker.getModifications());
		model.setAlertMods(alertModTracker.getModifications());
		model.setStepMods(jobStepMediator.getModifications());
		return model;
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
	public void setModel(MsJobModel model) {
		this.model = model;
		final MsJob job = Objects.requireNonNull(model.job.getOld());
		String currentCategory = job.getCategoryName();
		isAlterMode = !DbUtils.defaultId.equals(model.job.getOld().getId());
		if (isAlterMode) {
			ownerTextField.setText(job.getOwnerName());
			createdTextField.setText(job.getDateCreated());
			lastModifiedTextField.setText(job.getLastModified());
			lastExecutedTextField.setText(job.getLastExecuted());
			setTitle("Alter Job " + job.getName());
		} else {
			setTitle("Create Job");
		}
		nameTextField.setText(job.getName());
		descriptionTextArea.setText(job.getDescription());
		enabledCheckBox.setSelected(job.isEnabled());

		final List<BasicIdentity> jobCategories = model.categories;
		JComboBoxUtilKt.synchronizeByName(categoryComboBox, jobCategories, currentCategory, null);

		bindTab2Steps();
		bindTab3Schedule();
		bindTab4Alert();
		bindTab5Notification(model);

		for (UiDependencyRule uiRule : uiRules) {
			uiRule.apply();
		}
	}

	@Override
	public void activateSqlPreview(Function3<? super JPanel, ? super List<? extends JPanel>, ? super MsSqlScriptState, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel, stepsPanel, schedulesPanel, alertsPanel, notificationsPanel), scriptState);
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "JobDialog";
	}

	@Nullable
	@Override
	public String getModelHelpId() {
		return "ru.coding4fun.intellij.database.help.agent.job";
	}

	private MsNotifyLevel getSelectedLevel(JComboBox<BasicIdentity> comboBox) {
		MsNotifyLevel notifyLevel = null;
		final String selectedLevelId = ComboBoxGetter.INSTANCE.getSelected(comboBox, BasicIdentity::getId);
		if (selectedLevelId != null) {
			notifyLevel = Arrays.stream(MsNotifyLevel.values())
					.filter(level -> level.getId().equals(selectedLevelId))
					.findFirst().get();
		}
		return notifyLevel;
	}
}
