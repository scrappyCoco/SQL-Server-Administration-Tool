package ru.coding4fun.intellij.database.ui.form.security;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.security.CredentialGenerator;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.property.security.MsCredential;
import ru.coding4fun.intellij.database.model.property.security.MsCredentialModel;
import ru.coding4fun.intellij.database.ui.JComboBoxUtilKt;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.UiDependencyManager;
import ru.coding4fun.intellij.database.ui.form.UiDependencyRule;
import ru.coding4fun.intellij.database.ui.form.state.ComboBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.StateChanger;
import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter;

import javax.swing.*;
import java.util.List;

public class CredentialDialog extends JDialog implements ModelDialog<MsCredentialModel> {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField nameTextField;
	private JTextField identityTextField;
	private JPasswordField passwordField;
	private JCheckBox useEncryptionProviderCheckBox;
	private JComboBox<BasicIdentity> providerComboBox;
	private JLabel passwordLabel;
	private JLabel identityLabel;
	private JLabel credentialNameLabel;
	private JPanel sqlPreviewPanel;
	private JPanel generalPanel;

	public CredentialDialog() {
		this.setContentPane(contentPane);
		registerRules();
	}

	private void registerRules() {
		UiDependencyRule encryptionProviderRule = new UiDependencyRule(providerComboBox)
				.dependOn(useEncryptionProviderCheckBox)
				.must(useEncryptionProviderCheckBox::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyManager.Companion.register(encryptionProviderRule);
	}

	public MsCredential getNewModel() {
		return new MsCredential(
				nameTextField.getText(),
				identityTextField.getText(),
				"",
				TextFieldGetter.INSTANCE.getText(passwordField),
				"",
				ComboBoxGetter.INSTANCE.getText(providerComboBox)
		);
	}

	private MsCredentialModel model;
	@Override
	public MsCredentialModel getModel() {
		model.credential.setNew(getNewModel());
		return model;
	}

	@Override
	public void setModel(MsCredentialModel model) {
		this.model = model;
		String selectedCryptographicProvider = null;

		final MsCredential credential = model.credential.getOld();
		if (credential != null) {
			isAlterMode = true;
			nameTextField.setText(credential.getName());
			identityTextField.setText(credential.getIdentityName());
			selectedCryptographicProvider = credential.getProviderName();

			nameTextField.setEnabled(false);
			credentialNameLabel.setEnabled(false);
			useEncryptionProviderCheckBox.setEnabled(false);
			providerComboBox.setEnabled(false);

			setTitle("Alter Credential " + credential.getName());
		} else {
			setTitle("Create Credential");
		}

		JComboBoxUtilKt.synchronizeByName(providerComboBox, model.getCryptographicProviders(), selectedCryptographicProvider, useEncryptionProviderCheckBox);
	}

	private boolean isAlterMode;
	@Override
	public boolean isAlterMode() {
		return isAlterMode;
	}

	@NotNull
	@Override
	public ScriptGeneratorBase<MsCredentialModel> getScriptGenerator() {
		return CredentialGenerator.INSTANCE;
	}

	@Override
	public void activateSqlPreview(@NotNull Function2<? super JPanel, ? super List<? extends JPanel>, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel));
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "CredentialDialog";
	}

	@Nullable
	@Override
	public String getModelHelpId() {
		return "ru.coding4fun.intellij.database.help.security.credential";
	}
}
