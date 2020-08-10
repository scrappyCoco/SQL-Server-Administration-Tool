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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.intellij.database.data.property.DbUtils;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.security.AsymmetricKeyGenerator;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.property.security.MsAsymmetricKey;
import ru.coding4fun.intellij.database.model.property.security.MsAsymmetricKeyModel;
import ru.coding4fun.intellij.database.ui.DialogUtilsKt;
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
import java.util.Objects;

public class AsymmetricKeyDialog extends JDialog implements ModelDialog<MsAsymmetricKeyModel> {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField nameTextField;
	private JTextField authorizationTextField;
	private JTextField fileTextField;
	private JLabel fileLabel;
	private JTextField executableFileTextField;
	private JTextField assemblyTextField;
	private JLabel providerLabel;
	private JLabel executableFileLabel;
	private JTextField providerTextField;
	private JComboBox<BasicIdentity> algorithmComboBox;
	private JTextField providerKeyNameTextField;
	private JComboBox<BasicIdentity> creationDispositionComboBox;
	private JTextField passwordTextField;
	private JCheckBox algorithmCheckBox;
	private JCheckBox creationDispositionCheckBox;
	private JPanel sqlPreviewPanel;
	private JPanel generalPanel;
	private JLabel assemblyLabel;
	private JComboBox<BasicIdentity> dbComboBox;

	public AsymmetricKeyDialog() {
		this.setContentPane(contentPane);
		registerRules();
	}

	@SuppressWarnings("Duplicates")
	private void registerRules() {
		//region Key Source
		UiDependencyRule fileRule = new UiDependencyRule(fileLabel, fileTextField)
				.dependOn(executableFileTextField, assemblyTextField, providerTextField)
				.must(this::isProviderNotSelected, this::isAssemblyNotSelected, this::isExecutableFileNotSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule executableRule = new UiDependencyRule(executableFileLabel, executableFileTextField)
				.dependOn(fileTextField, assemblyTextField, providerTextField)
				.must(this::isFileNotSelected, this::isAssemblyNotSelected, this::isProviderNotSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule assemblyRule = new UiDependencyRule(assemblyLabel, assemblyTextField)
				.dependOn(fileTextField, executableFileTextField, providerTextField)
				.must(this::isFileNotSelected, this::isExecutableFileNotSelected, this::isProviderNotSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule providerRule = new UiDependencyRule(providerLabel, providerTextField)
				.dependOn(fileTextField, executableFileTextField, assemblyTextField)
				.must(this::isFileNotSelected, this::isExecutableFileNotSelected, this::isAssemblyNotSelected)
				.setStateChanger(StateChanger::enable);
		//endregion

		UiDependencyRule algorithmRule = new UiDependencyRule(algorithmComboBox)
				.dependOn(algorithmCheckBox)
				.must(algorithmCheckBox::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule creationDispositionRule = new UiDependencyRule(creationDispositionComboBox)
				.dependOn(creationDispositionCheckBox)
				.must(creationDispositionCheckBox::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyManager.Companion.register(fileRule, executableRule, assemblyRule, providerRule,
				algorithmRule, creationDispositionRule);
	}

	private boolean isAssemblyNotSelected() {
		return assemblyTextField.getText().isEmpty() || !assemblyTextField.isEnabled();
	}

	@NotNull
	private Boolean isExecutableFileNotSelected() {
		return executableFileTextField.getText().isEmpty() || !executableFileTextField.isEnabled();
	}

	@NotNull
	private Boolean isFileNotSelected() {
		return fileTextField.getText().isEmpty() || !fileTextField.isEnabled();
	}

	@NotNull
	private Boolean isProviderNotSelected() {
		return providerTextField.getText().isEmpty() || !providerTextField.isEnabled();
	}

	@NotNull
	@Contract(" -> new")
	private MsAsymmetricKey getNewModel() {
		return new MsAsymmetricKey(
				DbUtils.defaultId,
				nameTextField.getText(),
				TextFieldGetter.INSTANCE.getText(authorizationTextField),
				TextFieldGetter.INSTANCE.getText(fileTextField),
				TextFieldGetter.INSTANCE.getText(executableFileTextField),
				TextFieldGetter.INSTANCE.getText(assemblyTextField),
				TextFieldGetter.INSTANCE.getText(providerTextField),
				ComboBoxGetter.INSTANCE.getText(algorithmComboBox),
				TextFieldGetter.INSTANCE.getText(providerKeyNameTextField),
				ComboBoxGetter.INSTANCE.getText(creationDispositionComboBox),
				TextFieldGetter.INSTANCE.getText(passwordTextField),
				Objects.requireNonNull(ComboBoxGetter.INSTANCE.getText(dbComboBox))
		);
	}

	private void setOriginalModel(@NotNull MsAsymmetricKey asymmetricKey) {
		nameTextField.setText(asymmetricKey.getName());
		authorizationTextField.setText(asymmetricKey.getAuthorization());
		fileTextField.setText(asymmetricKey.getFile());
		executableFileTextField.setText(asymmetricKey.getExecutableFile());
		assemblyTextField.setText(asymmetricKey.getAssembly());
		providerTextField.setText(asymmetricKey.getProvider());
		providerKeyNameTextField.setText(asymmetricKey.getProviderKeyName());
		passwordTextField.setText(passwordTextField.getText());
	}

	@NotNull
	public JPanel getSqlPreviewPanel() {
		return sqlPreviewPanel;
	}

	@NotNull
	@Override
	public MsAsymmetricKeyModel getModel() {
		model.asymKey.setNew(getNewModel());
		return model;
	}

	private MsAsymmetricKeyModel model;

	@Override
	public void setModel(@NotNull MsAsymmetricKeyModel model) {
		this.model = model;

		String algorithm = null;
		String creationDisposition = null;

		final MsAsymmetricKey asymmetricKey = model.asymKey.getOld();
		isAlterMode = !DbUtils.defaultId.equals(asymmetricKey.getId());

		String db = !isAlterMode ? null : asymmetricKey.getDb();
		JComboBoxUtilKt.synchronizeByName(dbComboBox, model.databases, db, null);

		if (isAlterMode) {
			setOriginalModel(asymmetricKey);
			algorithm = asymmetricKey.getAlgorithm();
			creationDisposition = asymmetricKey.getCreationDisposition();
			setTitle("Alter Asymmetric Key " + asymmetricKey.getName());
		} else {
			setTitle("Create Asymmetric Key");
		}

		JComboBoxUtilKt.synchronizeById(algorithmComboBox, model.algorithms, algorithm, algorithmCheckBox);

		JComboBoxUtilKt.synchronizeById(creationDispositionComboBox,
				model.creationDispositions,
				creationDisposition,
				creationDispositionCheckBox
		);

		if (isAlterMode) {
			DialogUtilsKt.disableAll(this);
		}
	}

	@NotNull
	@Override
	public ScriptGeneratorBase<MsAsymmetricKeyModel> getScriptGenerator() {
		return AsymmetricKeyGenerator.INSTANCE;
	}

	@Override
	public void activateSqlPreview(@NotNull Function3<? super JPanel, ? super List<? extends JPanel>, ? super MsSqlScriptState, Unit> activationFun) {
		activationFun.invoke(sqlPreviewPanel, List.of(generalPanel), null);
	}

	private boolean isAlterMode;
	@Override
	public boolean isAlterMode() {
		return isAlterMode;
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "AsymmetricKeyDialog";
	}

	@Nullable
	@Override
	public String getModelHelpId() {
		return "ru.coding4fun.intellij.database.help.security.asymmetric.key";
	}
}