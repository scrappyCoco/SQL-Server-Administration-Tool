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

package ru.coding4fun.intellij.database.ui.form.security;

import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.intellij.database.data.property.DbUtils;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.security.CredentialGenerator;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.property.security.MsCredential;
import ru.coding4fun.intellij.database.model.property.security.MsCredentialModel;
import ru.coding4fun.intellij.database.ui.JComboBoxUtilKt;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.MsSqlScriptState;
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
		nameTextField.setText(credential.getName());
		identityTextField.setText(credential.getIdentityName());
		selectedCryptographicProvider = credential.getProviderName();

		if (!DbUtils.defaultId.equals(credential.getId())) {
			isAlterMode = true;
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
	public void activateSqlPreview(@NotNull Function3<? super JPanel, ? super List<? extends JPanel>, ? super MsSqlScriptState, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel), null);
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
