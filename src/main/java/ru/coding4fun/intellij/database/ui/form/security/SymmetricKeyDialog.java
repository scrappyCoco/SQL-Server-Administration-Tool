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
import ru.coding4fun.intellij.database.generation.security.SymmetricKeyGenerator;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.property.security.MsSymmetricKey;
import ru.coding4fun.intellij.database.model.property.security.MsSymmetricKeyModel;
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

public class SymmetricKeyDialog extends JDialog implements ModelDialog<MsSymmetricKeyModel> {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JLabel nameLabel;
	private JTextField nameTextField;
	private JLabel authorizationLabel;
	private JTextField authorizationTextField;
	private JTextField providerTextField;
	private JTextField keySourceTextField;
	private JLabel identityValueLabel;
	private JTextField identityValueTextField;
	private JTextField providerKeyNameTextField;
	private JLabel providerKeyNameLabel;
	private JLabel certificateLabel;
	private JTextField certificateTextField;
	private JTextField passwordTextField;
	private JLabel passwordLabel;
	private JTextField symmetricKeyTextField;
	private JTextField asymmetricKeyTextField;
	private JComboBox<BasicIdentity> algorithmComboBox;
	private JComboBox<BasicIdentity> creationDispositionComboBox;
	private JLabel keySourceLabel;
	private JLabel symmetricKeyLabel;
	private JLabel asymmetricKeyLabel;
	private JLabel encryptionMechanismLabel;
	private JLabel providerLabel;
	private JLabel keyOptionsLabel;
	private JCheckBox algorithmCheckBox;
	private JCheckBox creationDispositionCheckBox;
	private JPanel sqlPreviewPanel;
	private JPanel generalPanel;
	private JComboBox<BasicIdentity> dbComboBox;

	public SymmetricKeyDialog() {
		this.setContentPane(contentPane);
		registerRules();
	}

	private void registerRules() {
		UiDependencyRule algorithmRule = new UiDependencyRule(algorithmComboBox)
				.dependOn(algorithmCheckBox)
				.must(algorithmCheckBox::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule creationDispositionRule = new UiDependencyRule(creationDispositionComboBox)
				.dependOn(creationDispositionCheckBox)
				.must(creationDispositionCheckBox::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyManager.Companion.register(algorithmRule, creationDispositionRule);
	}

	public MsSymmetricKey getNewModel() {
		if (isAlterMode) return null;

		return new MsSymmetricKey(
				"",
				nameTextField.getText(),
				TextFieldGetter.INSTANCE.getText(authorizationTextField),
				TextFieldGetter.INSTANCE.getText(providerTextField),
				TextFieldGetter.INSTANCE.getText(keySourceTextField),
				ComboBoxGetter.INSTANCE.getText(algorithmComboBox),
				TextFieldGetter.INSTANCE.getText(identityValueTextField),
				TextFieldGetter.INSTANCE.getText(providerKeyNameTextField),
				ComboBoxGetter.INSTANCE.getText(creationDispositionComboBox),
				TextFieldGetter.INSTANCE.getText(certificateTextField),
				TextFieldGetter.INSTANCE.getText(passwordTextField),
				TextFieldGetter.INSTANCE.getText(symmetricKeyTextField),
				TextFieldGetter.INSTANCE.getText(asymmetricKeyTextField),
				Objects.requireNonNull(ComboBoxGetter.INSTANCE.getText(dbComboBox))
		);
	}


	private MsSymmetricKeyModel model;
	@Override
	public MsSymmetricKeyModel getModel() {
		model.key.setNew(getNewModel());
		return model;
	}

	@Override
	public void setModel(MsSymmetricKeyModel model) {
		this.model = model;
		final MsSymmetricKey symmetricKey = model.key.getOld();
		isAlterMode = !DbUtils.defaultId.equals(symmetricKey.getId());

		String db = !isAlterMode ? null : symmetricKey.getDb();
		JComboBoxUtilKt.synchronizeByName(dbComboBox, model.databases, db, null);

		if (isAlterMode) {
			nameTextField.setText(symmetricKey.getName());
			setTitle("Alter Symmetric Key " + symmetricKey.getName());
			DialogUtilsKt.disableAll(contentPane);
		} else {
			setTitle("Create Symmetric Key");
		}
	}

	private boolean isAlterMode;
	@Override
	public boolean isAlterMode() {
		return isAlterMode;
	}

	@NotNull
	@Override
	public ScriptGeneratorBase<MsSymmetricKeyModel> getScriptGenerator() {
		return SymmetricKeyGenerator.INSTANCE;
	}

	@Override
	public void activateSqlPreview(Function3<? super JPanel, ? super List<? extends JPanel>, ? super MsSqlScriptState, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel), null);
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "SymmetricKeyDialog";
	}

	@Nullable
	@Override
	public String getModelHelpId() {
		return "ru.coding4fun.intellij.database.help.security.symmetric.key";
	}
}
