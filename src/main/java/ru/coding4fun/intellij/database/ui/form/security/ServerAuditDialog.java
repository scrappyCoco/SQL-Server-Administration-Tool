package ru.coding4fun.intellij.database.ui.form.security;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.intellij.database.data.property.DbNull;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.security.ServerAuditGenerator;
import ru.coding4fun.intellij.database.model.property.security.MsServerAudit;
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditDestination;
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditModel;
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditOnFailureKind;
import ru.coding4fun.intellij.database.ui.JComboBoxUtilKt;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.UiDependencyManager;
import ru.coding4fun.intellij.database.ui.form.UiDependencyRule;
import ru.coding4fun.intellij.database.ui.form.state.CheckBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.RadioButtonGetter;
import ru.coding4fun.intellij.database.ui.form.state.StateChanger;
import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ServerAuditDialog extends JDialog implements ModelDialog<MsServerAuditModel> {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField auditNameTextField;
	private JTextField queueDelayTextField;
	private JRadioButton continueRadioButton;
	private JRadioButton shutDownServerRadioButton;
	private JComboBox<MsServerAuditDestination> auditDestinationComboBox;
	private JTextField filePathTextField;
	private JRadioButton maximumRolloverFilesRadioButton;
	private JCheckBox unlimitedAuditMaxFileLimitCheckBox;
	private JRadioButton maximumOfFilesRadioButton;
	private JTextField numberOfFilesTextField;
	private JTextField maxFileSizeTextField;
	private JRadioButton mbRadioButton;
	private JRadioButton gbRadioButton;
	private JRadioButton tbRadioButton;
	private JCheckBox unlimitedMaxFileSizeCheckBox;
	private JCheckBox reserveDiskSpaceCheckBox;
	private JLabel filePathLabel;
	private JLabel maxFileSizeLabel;
	private JLabel auditFileMaxLimitLabel;
	private JLabel numberOfFileLabel;
	private JRadioButton failOperationRadioButton;
	private JPanel sqlPreviewPanel;
	private JPanel generalPanel;
	private JPanel filterPanel;
	private JLabel destinationLabel;
	private JLabel logFailure;
	private JLabel delayLabel;
	private JLabel nameLabel;

	public ServerAuditDialog() {
		this.setContentPane(contentPane);
		registerRules();
	}

	private void registerRules() {
		UiDependencyRule unlimitedAuditMaxFileLimitRule = new UiDependencyRule(unlimitedAuditMaxFileLimitCheckBox)
				.must(this::fileAuditDestinationSelected, maximumRolloverFilesRadioButton::isSelected)
				.dependOn(auditDestinationComboBox, maximumRolloverFilesRadioButton, maximumOfFilesRadioButton)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule filePathRule = new UiDependencyRule(filePathLabel, filePathTextField,
				auditFileMaxLimitLabel, maximumRolloverFilesRadioButton, maximumOfFilesRadioButton,
				numberOfFileLabel,
				maxFileSizeLabel, maxFileSizeTextField, unlimitedMaxFileSizeCheckBox, reserveDiskSpaceCheckBox
		)
				.must(this::fileAuditDestinationSelected)
				.dependOn(auditDestinationComboBox)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule numberOfFilesRule = new UiDependencyRule(numberOfFileLabel, numberOfFilesTextField)
				.must(this::fileAuditDestinationSelected, this::unlimitedMaxFileIsNotSelected)
				.dependOn(auditDestinationComboBox, maximumRolloverFilesRadioButton, maximumOfFilesRadioButton, unlimitedAuditMaxFileLimitCheckBox)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule sizeRule = new UiDependencyRule(mbRadioButton, gbRadioButton, tbRadioButton, maxFileSizeTextField)
				.must(this::fileAuditDestinationSelected, this::unlimitedMaxFileSizeIsNotSelected)
				.dependOn(auditDestinationComboBox, unlimitedMaxFileSizeCheckBox)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule nameRule = new UiDependencyRule(queueDelayTextField, continueRadioButton, shutDownServerRadioButton,
				failOperationRadioButton, auditDestinationComboBox, filePathTextField, maximumRolloverFilesRadioButton,
				maximumOfFilesRadioButton, numberOfFilesTextField, maxFileSizeTextField, mbRadioButton, gbRadioButton,
				tbRadioButton, unlimitedMaxFileSizeCheckBox, reserveDiskSpaceCheckBox, maxFileSizeLabel, numberOfFileLabel,
				auditFileMaxLimitLabel, filePathLabel, destinationLabel, logFailure, delayLabel, nameLabel)
				.dependOn(auditNameTextField)
				.must(this::isNameChanged)
				.setStateChanger(StateChanger::disable);

		UiDependencyManager.Companion.register(unlimitedAuditMaxFileLimitRule, filePathRule, numberOfFilesRule,
				sizeRule, nameRule);
	}

	private Boolean isNameChanged() {
		final MsServerAudit old = model.audit.getOld();
		if (old == null) return false;
		return !old.getName().equals(TextFieldGetter.INSTANCE.getText(auditNameTextField));
	}

	private Boolean unlimitedMaxFileIsNotSelected() {
		return !CheckBoxGetter.INSTANCE.apply(unlimitedAuditMaxFileLimitCheckBox);
	}

	private Boolean unlimitedMaxFileSizeIsNotSelected() {
		return !unlimitedMaxFileSizeCheckBox.isSelected();
	}

	@NotNull
	private Boolean fileAuditDestinationSelected() {
		final MsServerAuditDestination selectedDestination = getAuditDestination();
		return MsServerAuditDestination.FILE.equals(selectedDestination);
	}

	public MsServerAudit getNewModel() {
		Long maxSize;
		if (CheckBoxGetter.INSTANCE.apply(unlimitedMaxFileSizeCheckBox)) {
			maxSize = -1L; // Unlimited.
		} else {
			maxSize = TextFieldGetter.INSTANCE.getLong(maxFileSizeTextField);
		}

		Integer numberOfFiles = null;
		Integer numberRolloverOfFiles = null;
		if (CheckBoxGetter.INSTANCE.apply(unlimitedAuditMaxFileLimitCheckBox)) {
			numberRolloverOfFiles = -1;
		} else {
			if (RadioButtonGetter.INSTANCE.apply(maximumOfFilesRadioButton)) {
				numberOfFiles = TextFieldGetter.INSTANCE.getInt(numberOfFilesTextField);
			} else if (RadioButtonGetter.INSTANCE.apply(maximumRolloverFilesRadioButton)) {
				numberRolloverOfFiles = TextFieldGetter.INSTANCE.getInt(numberOfFilesTextField);
			}
		}


		return new MsServerAudit(
				DbNull.value,
				TextFieldGetter.INSTANCE.getTextOrCompute(auditNameTextField, () -> ""),
				true,
				TextFieldGetter.INSTANCE.getInt(queueDelayTextField),
				getOnLogFailure(),
				getAuditDestination(),
				TextFieldGetter.INSTANCE.getText(filePathTextField),
				maxSize,
				getMaxSizeUnit(),
				numberRolloverOfFiles,
				numberOfFiles,
				CheckBoxGetter.INSTANCE.apply(reserveDiskSpaceCheckBox)
		);
	}

	private MsServerAuditOnFailureKind getOnLogFailure() {
		if (continueRadioButton.isSelected()) return MsServerAuditOnFailureKind.CONTINUE;
		if (shutDownServerRadioButton.isSelected()) return MsServerAuditOnFailureKind.SHUTDOWN;
		return MsServerAuditOnFailureKind.FAIL_OPERATION;
	}

	private MsServerAuditDestination getAuditDestination() {
		return (MsServerAuditDestination) Objects.requireNonNull(auditDestinationComboBox.getSelectedItem());
	}

	@NotNull
	private String getMaxSizeUnit() {
		if (mbRadioButton.isSelected()) return "MB";
		if (gbRadioButton.isSelected()) return "GB";
		return "TB";
	}

	private MsServerAuditModel model;

	@Override
	public MsServerAuditModel getModel() {
		model.audit.setNew(getNewModel());
		return model;
	}

	@Override
	public void setModel(MsServerAuditModel model) {
		final List<MsServerAuditDestination> destinations = Arrays.asList(MsServerAuditDestination.values());
		MsServerAuditDestination currentDestination = MsServerAuditDestination.FILE;

		this.model = model;
		final MsServerAudit audit = model.audit.getOld();
		if (audit != null) {
			isAlterMode = true;
			setTitle("Alter Server Audit " + audit.getName());

			auditNameTextField.setText(audit.getName());
			if (audit.getQueueDelay() != null) {
				queueDelayTextField.setText(audit.getQueueDelay().toString());
			}

			final MsServerAuditOnFailureKind onAuditLogFailure = audit.getOnAuditLogFailure();
			if (onAuditLogFailure == MsServerAuditOnFailureKind.SHUTDOWN)
				shutDownServerRadioButton.setSelected(true);
			else if (onAuditLogFailure == MsServerAuditOnFailureKind.CONTINUE) continueRadioButton.setSelected(true);
			else failOperationRadioButton.setSelected(true);

			currentDestination = audit.getAuditDestination();
			filePathTextField.setText(audit.getFilePath());

			if (audit.getMaxFiles() != null) {
				maximumOfFilesRadioButton.setSelected(true);
				numberOfFilesTextField.setText(audit.getMaxFiles().toString());
			} else {
				maximumRolloverFilesRadioButton.setSelected(true);
				if (audit.getMaxRolloverFiles() != null) {
					numberOfFilesTextField.setText(audit.getMaxRolloverFiles().toString());
				} else {
					unlimitedAuditMaxFileLimitCheckBox.setSelected(true);
				}
			}

			if (audit.getMaxFiles() == null) {
				unlimitedMaxFileSizeCheckBox.setSelected(true);
			} else {
				maxFileSizeTextField.setText(audit.getMaxFiles().toString());
			}

			if (audit.getReserveDiskSpace() != null) {
				reserveDiskSpaceCheckBox.setSelected(audit.getReserveDiskSpace());
			}

		} else {
			setTitle("Create Server Audit");
		}

		JComboBoxUtilKt.synchronize(
				auditDestinationComboBox,
				destinations,
				null,
				currentDestination::equals
		);


	}

	private boolean isAlterMode;

	@Override
	public boolean isAlterMode() {
		return isAlterMode;
	}

	@NotNull
	@Override
	public ScriptGeneratorBase<MsServerAuditModel> getScriptGenerator() {
		return ServerAuditGenerator.INSTANCE;
	}

	@Override
	public void activateSqlPreview(@NotNull Function2<? super JPanel, ? super List<? extends JPanel>, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel));
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "ServerAuditDialog";
	}

	@Nullable
	@Override
	public String getModelHelpId() {
		return "ru.coding4fun.intellij.database.help.security.server.audit";
	}
}