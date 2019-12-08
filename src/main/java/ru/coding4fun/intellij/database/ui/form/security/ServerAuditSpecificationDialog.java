package ru.coding4fun.intellij.database.ui.form.security;

import com.intellij.ui.CheckBoxList;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.security.ServerAuditSpecificationGenerator;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditSpecModel;
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditSpecification;
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditSpecificationAction;
import ru.coding4fun.intellij.database.ui.JComboBoxUtilKt;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.UiDependencyManager;
import ru.coding4fun.intellij.database.ui.form.UiDependencyRule;
import ru.coding4fun.intellij.database.ui.form.common.CheckBoxListModTracker;
import ru.coding4fun.intellij.database.ui.form.common.ModificationTracker;
import ru.coding4fun.intellij.database.ui.form.state.CheckBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.ComboBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.StateChanger;

import javax.swing.*;
import java.util.List;

public class ServerAuditSpecificationDialog extends JDialog implements ModelDialog<MsServerAuditSpecModel> {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JComboBox<BasicIdentity> auditComboBox;
	private JTextField nameTextField;
	private JLabel nameLabel;
	private JCheckBox isEnabledCheckBox;
	private JCheckBox auditCheckBox;
	private JPanel sqlPreviewPanel;
	private JPanel generalPanel;
	private JScrollPane actionsScrollPane;
	private CheckBoxList<MsServerAuditSpecificationAction> actionsList;
	private ModificationTracker<MsServerAuditSpecificationAction> actionListModificationTracker;

	public ServerAuditSpecificationDialog() {
		this.setContentPane(contentPane);
		registerRules();
	}

	private void registerRules() {
		UiDependencyRule auditRule = new UiDependencyRule(auditComboBox)
				.dependOn(auditCheckBox)
				.must(auditCheckBox::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyManager.Companion.register(auditRule);
	}

	public MsServerAuditSpecification getNewModel() {
		return new MsServerAuditSpecification(
				"",
				nameTextField.getText(),
				CheckBoxGetter.INSTANCE.apply(isEnabledCheckBox),
				ComboBoxGetter.INSTANCE.getText(auditComboBox)
		);
	}

	private MsServerAuditSpecModel model;

	@Override
	public MsServerAuditSpecModel getModel() {
		model.spec.setNew(getNewModel());
		model.setActions(actionListModificationTracker.getModifications());
		return model;
	}

	@Override
	public void setModel(MsServerAuditSpecModel model) {
		this.model = model;

		final List<MsServerAuditSpecificationAction> serverAuditSpecificationActions = model.defaultActions;
		actionListModificationTracker = new CheckBoxListModTracker<>(actionsScrollPane, serverAuditSpecificationActions);
		String auditName = null;
		final MsServerAuditSpecification spec = model.spec.getOld();

		if (spec == null) {
			setTitle("Create Server Audit Specification");
		} else {
			isAlterMode = true;
			nameTextField.setText(spec.getName());
			auditName = spec.getAuditName();
			isEnabledCheckBox.setSelected(spec.isEnabled());

			nameLabel.setEnabled(false);
			nameTextField.setEnabled(false);

			setTitle("Alter Server Audit Specification " + spec.getName());
		}

		final List<BasicIdentity> serverAudits = model.defaultServerAudits;
		JComboBoxUtilKt.synchronizeByName(auditComboBox, serverAudits, auditName, auditCheckBox);
	}

	private boolean isAlterMode;

	@Override
	public boolean isAlterMode() {
		return isAlterMode;
	}

	@NotNull
	@Override
	public ScriptGeneratorBase<MsServerAuditSpecModel> getScriptGenerator() {
		return ServerAuditSpecificationGenerator.INSTANCE;
	}

	@Override
	public void activateSqlPreview(@NotNull Function2<? super JPanel, ? super List<? extends JPanel>, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel));
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "ServerAuditSpecificationDialog";
	}
}
