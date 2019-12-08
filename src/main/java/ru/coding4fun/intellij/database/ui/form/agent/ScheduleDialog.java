package ru.coding4fun.intellij.database.ui.form.agent;

import com.intellij.ui.CheckBoxList;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.agent.ScheduleGenerator;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.property.agent.schedule.MsSchedule;
import ru.coding4fun.intellij.database.model.property.agent.schedule.MsScheduleJob;
import ru.coding4fun.intellij.database.model.property.agent.schedule.MsScheduleModel;
import ru.coding4fun.intellij.database.ui.JComboBoxUtilKt;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.UiDependencyManager;
import ru.coding4fun.intellij.database.ui.form.UiDependencyRule;
import ru.coding4fun.intellij.database.ui.form.agent.schedule.*;
import ru.coding4fun.intellij.database.ui.form.common.CheckBoxListModTracker;
import ru.coding4fun.intellij.database.ui.form.common.ModificationTracker;
import ru.coding4fun.intellij.database.ui.form.state.*;
import ru.coding4fun.intellij.database.ui.format.DateFormatter;
import ru.coding4fun.intellij.database.ui.format.TimeFormatter;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScheduleDialog extends JDialog implements ModelDialog<MsScheduleModel> {
	private JPanel contentPane;
	private JTextField nameTextField;
	private JComboBox<BasicIdentity> typeComboBox;
	private JCheckBox enabledCheckBox;
	private JFormattedTextField oneTimeDateTextField;
	private JFormattedTextField oneTimeTextField;
	private JComboBox<BasicIdentity> occursComboBox;
	private JFormattedTextField monthNumberTextField;
	private JFormattedTextField dayNumberTextField;
	private JComboBox<BasicIdentity> weekNumberComboBox;
	private JComboBox<BasicIdentity> dayOfWeekComboBox;
	private JFormattedTextField everyMonthNumberTextField;
	private JFormattedTextField recursEveryWeekTextField;
	private JPanel weeklyPanel;
	private JCheckBox mondayCheckBox;
	private JCheckBox tuesdayCheckBox;
	private JCheckBox wednesdayCheckBox;
	private JCheckBox thursdayCheckBox;
	private JCheckBox fridayCheckBox;
	private JCheckBox saturdayCheckBox;
	private JCheckBox sundayCheckBox;
	private JPanel dailyPanel;
	private JFormattedTextField occursEveryTextField;
	private JRadioButton occursOnceAtRadioButton;
	private JRadioButton occursEveryRadioButton;
	private JFormattedTextField occursOnceAtTextField;
	private JFormattedTextField occursDailyEveryTextField;
	private JComboBox<BasicIdentity> timeComboBox;
	private JFormattedTextField startingAtTextField;
	private JFormattedTextField endingAtTextField;
	private JFormattedTextField startDateTextField;
	private JFormattedTextField endDateTextField;
	private JPanel oneTimeOccurrencePanel;
	private JPanel frequencyPanel;
	private JPanel dailyFrequencyPanel;
	private JPanel durationPanel;
	private JCheckBox endDateCheckBox;
	private JPanel monthPanel;
	private JPanel monthRelativePanel;
	private JTextField ownerTextField;
	//private JList<MsScheduleJob> jobList;
	private JPanel occursEveryPanel;
	private JPanel sqlPreviewPanel;
	private JPanel jobsPanel;
	private JPanel generalPanel;

	private ModificationTracker<MsScheduleJob> jobs;
	private Map<Integer, JCheckBox> weekToCheckBoxMap;
	private DateFormatter dateFormatter;
	private TimeFormatter timeFormatter;
	private ScheduleType scheduleType;
	private OccursType occursType;
	private TimeType timeType;
	private WeekType weekType;
	private List<UiDependencyRule> rules;
	private CheckBoxList<MsScheduleJob> jobList;
	private JScrollPane jobScrollPane;

	public ScheduleDialog() {
		this.setContentPane(contentPane);
		scheduleType = new ScheduleType(typeComboBox);

		weekToCheckBoxMap = Map.of(1, sundayCheckBox,
				2, mondayCheckBox,
				4, tuesdayCheckBox,
				8, wednesdayCheckBox,
				16, thursdayCheckBox,
				32, fridayCheckBox,
				64, saturdayCheckBox
		);

		occursType = new OccursType(occursComboBox, scheduleType, weekToCheckBoxMap,
				occursEveryTextField, monthNumberTextField, everyMonthNumberTextField);

		timeType = new TimeType(timeComboBox, scheduleType);
		weekType = new WeekType(dayOfWeekComboBox);

		registerRules();
		registerFormatters();
	}

	private void registerFormatters() {
		final NumberFormatter numberFormatter = new NumberFormatter();
		numberFormatter.setAllowsInvalid(false);

		dateFormatter = DateFormatter.INSTANCE;
		timeFormatter = TimeFormatter.INSTANCE;

		// date.
		final DefaultFormatterFactory dateFormatterFactory = new DefaultFormatterFactory(dateFormatter);
		oneTimeDateTextField.setFormatterFactory(dateFormatterFactory);
		startDateTextField.setFormatterFactory(dateFormatterFactory);
		endDateTextField.setFormatterFactory(dateFormatterFactory);

		// time.
		final DefaultFormatterFactory timeFormatterFactory = new DefaultFormatterFactory(timeFormatter);
		occursOnceAtTextField.setFormatterFactory(timeFormatterFactory);
		oneTimeTextField.setFormatterFactory(timeFormatterFactory);
		startingAtTextField.setFormatterFactory(timeFormatterFactory);
		endingAtTextField.setFormatterFactory(timeFormatterFactory);

		// number.
		final DefaultFormatterFactory numberFormatterFactory = new DefaultFormatterFactory(numberFormatter);
		JFormattedTextField[] numberFields = new JFormattedTextField[]{
				occursEveryTextField,
				occursDailyEveryTextField,
				recursEveryWeekTextField,
				dayNumberTextField,
				monthNumberTextField,
				everyMonthNumberTextField
		};
		for (JFormattedTextField numberField : numberFields) {
			numberField.setFormatterFactory(numberFormatterFactory);
			numberField.setText("1");
		}
	}

	private void registerRules() {
		UiDependencyRule oneTimeOccurrence = new UiDependencyRule(oneTimeOccurrencePanel)
				.dependOn(typeComboBox)
				.must(scheduleType::isOneTime)
				.setStateChanger(StateChanger::enableRecurse);

		UiDependencyRule recurseOccurrenceRule = new UiDependencyRule(dailyFrequencyPanel, durationPanel)
				.dependOn(typeComboBox)
				.must(scheduleType::isRecurring)
				.setStateChanger(StateChanger::enableRecurse);

		UiDependencyRule occurrenceRule = new UiDependencyRule(frequencyPanel)
				.dependOn(typeComboBox)
				.must(scheduleType::isRecurring)
				.setStateChanger(StateChanger::enableRecurse);

		UiDependencyRule recurseDailyRule = new UiDependencyRule(dailyPanel)
				.dependOn(typeComboBox, occursComboBox)
				.must(occursType::isDailySelected)
				.setStateChanger(StateChanger::enableRecurse);

		UiDependencyRule recurseWeeklyRule = new UiDependencyRule(weeklyPanel)
				.dependOn(typeComboBox, occursComboBox)
				.must(occursType::isWeeklySelected)
				.setStateChanger(StateChanger::enableRecurse);

		UiDependencyRule monthRelativeRule = new UiDependencyRule(monthRelativePanel)
				.dependOn(typeComboBox, occursComboBox)
				.must(occursType::isMonthlyRelativeSelected)
				.setStateChanger(StateChanger::enableRecurse);

		UiDependencyRule monthRule = new UiDependencyRule(monthPanel)
				.dependOn(typeComboBox, occursComboBox)
				.must(occursType::isMonthlySelected)
				.setStateChanger(StateChanger::enableRecurse);

		UiDependencyRule occursOnceRule = new UiDependencyRule(occursOnceAtTextField)
				.dependOn(occursOnceAtRadioButton, occursEveryRadioButton)
				.must(this::isOccursOnceSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule occursEveryRule = new UiDependencyRule(occursEveryPanel)
				.dependOn(occursOnceAtRadioButton, occursEveryRadioButton)
				.must(this::isOccursEverySelected)
				.setStateChanger(StateChanger::enableRecurse);

		UiDependencyRule endDateRule = new UiDependencyRule(endDateTextField)
				.dependOn(endDateCheckBox)
				.must(this::isEndDateSelected)
				.setStateChanger(StateChanger::enable);

		rules = UiDependencyManager.Companion.register(oneTimeOccurrence, occurrenceRule, recurseOccurrenceRule,
				recurseDailyRule, recurseWeeklyRule,
				monthRelativeRule, monthRule, occursOnceRule, occursEveryRule, endDateRule);
	}

	private Boolean isOccursOnceSelected() {
		return RadioButtonGetter.INSTANCE.apply(occursOnceAtRadioButton);
	}

	private Boolean isOccursEverySelected() {
		return RadioButtonGetter.INSTANCE.apply(occursEveryRadioButton);
	}

	private Boolean isEndDateSelected() {
		return CheckBoxGetter.INSTANCE.apply(endDateCheckBox);
	}

	public MsSchedule getNewModel() {
		final MsSchedule oldSchedule = model.schedule.getOld();
		return new MsSchedule(oldSchedule == null ? "" : oldSchedule.getId(),
				TextFieldGetter.INSTANCE.getTextOrCompute(nameTextField, () -> "My Schedule"),
				enabledCheckBox.isSelected(),
				getFreqType(),
				getFreqInterval(),
				timeType.getFreqSubDayType(),
				TextFieldGetter.INSTANCE.getIntOrCompute(occursDailyEveryTextField, () -> 0),
				weekType.getFreqRelativeInterval(),
				occursType.getFreqRecurrenceFactor(),
				dateFormatter.getInt(startDateTextField),
				dateFormatter.getInt(endDateTextField),
				timeFormatter.getInt(startingAtTextField),
				timeFormatter.getInt(endingAtTextField),
				TextFieldGetter.INSTANCE.getText(ownerTextField));
	}

	private int getFreqInterval() {
		if (occursType.isDailySelected()) {
			return TextFieldGetter.INSTANCE.getIntOrCompute(occursEveryTextField, () -> 0);
		} else if (occursType.isWeeklySelected()) {
			int dayAccumulator = 0;

			if (CheckBoxGetter.INSTANCE.apply(mondayCheckBox)) {
				dayAccumulator += 2;
			}
			if (CheckBoxGetter.INSTANCE.apply(tuesdayCheckBox)) {
				dayAccumulator += 4;
			}
			if (CheckBoxGetter.INSTANCE.apply(wednesdayCheckBox)) {
				dayAccumulator += 8;
			}
			if (CheckBoxGetter.INSTANCE.apply(thursdayCheckBox)) {
				dayAccumulator += 16;
			}
			if (CheckBoxGetter.INSTANCE.apply(fridayCheckBox)) {
				dayAccumulator += 32;
			}
			if (CheckBoxGetter.INSTANCE.apply(saturdayCheckBox)) {
				dayAccumulator += 64;
			}
			if (CheckBoxGetter.INSTANCE.apply(sundayCheckBox)) {
				dayAccumulator += 1;
			}

			return dayAccumulator;
		} else if (occursType.isMonthlySelected()) {
			return TextFieldGetter.INSTANCE.getIntOrCompute(dayNumberTextField, () -> 0);
		} else if (occursType.isMonthlyRelativeSelected()) {
			final BasicIdentity selectedDayOfWeek = ComboBoxGetter.INSTANCE.getSelected(dayOfWeekComboBox, identity -> identity);
			final String id = Objects.requireNonNull(selectedDayOfWeek).getId();
			return Integer.parseInt(id);
		}

		return 0;
	}

	private int getFreqType() {
		if (scheduleType.isOneTime()) {
			return 1;
		} else if (occursType.isDailySelected()) {
			return 4;
		} else if (occursType.isWeeklySelected()) {
			return 8;
		} else if (occursType.isMonthlySelected()) {
			return 16;
		} else if (occursType.isMonthlyRelativeSelected()) {
			return 32;
		} else if (scheduleType.isAgentStarts()) {
			return 64;
		} else if (scheduleType.isIdle()) {
			return 128;
		}

		return -1;
	}

	private MsScheduleModel model;
	@Override
	public MsScheduleModel getModel() {
		model.setJobModifications(jobs.getModifications());
		model.schedule.setNew(getNewModel());
		return model;
	}

	@Override
	public void setModel(@NotNull MsScheduleModel model) {
		this.model = model;
		final MsSchedule schedule = model.getSchedule().getOld();

		if (schedule != null) {
			isAlterMode = true;
			nameTextField.setText(schedule.getName());
			enabledCheckBox.setSelected(schedule.getEnabled());
			ownerTextField.setText(schedule.getOwnerLoginName());

			setTitle("Alter schedule " + schedule.getName());
		} else {
			setTitle("Create schedule");
		}
		this.jobs = new CheckBoxListModTracker<>(jobScrollPane, model.getJobs());
		final BasicIdentity selectedScheduleType = ScheduleType.Companion.get(schedule == null ? null : schedule.getFreqType());
		final BasicIdentity selectedOccursType = OccursType.Companion.get(schedule == null ? null : schedule.getFreqType());

		JComboBoxUtilKt.synchronizeById(
				typeComboBox,
				ScheduleType.Companion.getAll(),
				selectedScheduleType == null ? null : selectedScheduleType.getId(),
				null
		);

		JComboBoxUtilKt.synchronizeById(
				occursComboBox,
				OccursType.Companion.getAll(),
				selectedOccursType == null ? null : selectedOccursType.getId(),
				null
		);

		final Integer freqInterval = schedule == null ? null : schedule.getFreqInterval();
		// MonthlyRelative.
		final BasicIdentity dayOfWeek = DayOfWeek.Companion.get(freqInterval);
		final BasicIdentity weekType = schedule == null ? null : WeekType.Companion.get(schedule.getFreqRelativeInterval());

		if (schedule != null) {
			if (occursType.isWeeklySelected()) {
				for (Map.Entry<Integer, JCheckBox> dayEntry : weekToCheckBoxMap.entrySet()) {
					final Integer dayCode = dayEntry.getKey();
					final JCheckBox dayCheckBox = dayEntry.getValue();
					final boolean isDaySelected = (freqInterval & dayCode) == dayCode;
					dayCheckBox.setSelected(isDaySelected);
				}

				final String recurrenceFactor = Integer.toString(schedule.getFreqRecurrenceFactor());
				recursEveryWeekTextField.setText(recurrenceFactor);
			} else if (occursType.isDailySelected()) {
				final String recurrenceFactor = Integer.toString(schedule.getFreqRecurrenceFactor());
				occursEveryTextField.setText(recurrenceFactor);
			} else if (occursType.isMonthlySelected()) {
				dayNumberTextField.setText(Integer.toString(freqInterval));
				monthNumberTextField.setText(Integer.toString(schedule.getFreqRecurrenceFactor()));
			} else if (occursType.isMonthlyRelativeSelected()) {
				everyMonthNumberTextField.setText(Integer.toString(schedule.getFreqRecurrenceFactor()));
			}
		}

		JComboBoxUtilKt.synchronizeById(
				weekNumberComboBox,
				WeekType.Companion.getAll(),
				weekType == null ? null : weekType.getId(),
				null
		);

		JComboBoxUtilKt.synchronizeById(
				dayOfWeekComboBox,
				DayOfWeek.Companion.getAll(),
				dayOfWeek == null ? null : dayOfWeek.getId(),
				null
		);

		final String freqSubDayType = schedule == null ? null : Integer.toString(schedule.getFreqSubDayType());
		JComboBoxUtilKt.synchronizeById(
				timeComboBox,
				TimeType.Companion.getAll(),
				freqSubDayType,
				null
		);

		if (schedule != null) {
			dateFormatter.setDate(schedule.getActiveStartDate(), startDateTextField);
			if (schedule.getActiveEndDate() == 99991231) {
				endDateCheckBox.setSelected(false);
				endDateTextField.setEnabled(false);
			} else {
				endDateCheckBox.setSelected(true);
				dateFormatter.setDate(schedule.getActiveEndDate(), endDateTextField);
			}

			if ("1".equals(freqSubDayType)) {
				timeFormatter.setTime(schedule.getActiveStartTime(), occursOnceAtTextField);
				occursOnceAtRadioButton.setSelected(true);
			} else {
				occursEveryRadioButton.setSelected(true);
				occursDailyEveryTextField.setText(Integer.toString(schedule.getFreqSubDayInterval()));
				timeFormatter.setTime(schedule.getActiveStartTime(), startingAtTextField);
				timeFormatter.setTime(schedule.getActiveEndTime(), endingAtTextField);
			}

			if (scheduleType.isOneTime()) {
				dateFormatter.setDate(schedule.getActiveStartDate(), oneTimeDateTextField);
				timeFormatter.setTime(schedule.getActiveStartTime(), oneTimeTextField);
			}
		}

		for (UiDependencyRule rule : rules) {
			rule.apply();
		}
	}

	private boolean isAlterMode;
	@Override
	public boolean isAlterMode() {
		return isAlterMode;
	}

	@NotNull
	@Override
	public ScriptGeneratorBase<MsScheduleModel> getScriptGenerator() {
		return ScheduleGenerator.INSTANCE;
	}

	@Override
	public void activateSqlPreview(@NotNull Function2<? super JPanel, ? super List<? extends JPanel>, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel, jobsPanel));
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "ScheduleDialog";
	}
}