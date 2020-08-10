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
import ru.coding4fun.intellij.database.generation.security.CryptographicProviderGenerator;
import ru.coding4fun.intellij.database.model.property.security.MsCryptographicProvider;
import ru.coding4fun.intellij.database.model.property.security.MsCryptographicProviderModel;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.MsSqlScriptState;

import javax.swing.*;
import java.util.List;

public class CryptographicProviderDialog extends JDialog implements ModelDialog<MsCryptographicProviderModel> {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField fileTextField;
	private JTextField nameTextField;
	private JLabel nameLabel;
	private JPanel sqlPreviewPanel;
	private JPanel generalPanel;

	public CryptographicProviderDialog() {
		this.setContentPane(contentPane);
	}

	public MsCryptographicProvider getNewModel() {
		return new MsCryptographicProvider(
				nameTextField.getText(),
				"",
				fileTextField.getText(),
				true,
				true
		);
	}

	private MsCryptographicProviderModel model;
	@Override
	public MsCryptographicProviderModel getModel() {
		model.provider.setNew(getNewModel());
		return model;
	}

	@Override
	public void setModel(MsCryptographicProviderModel model) {
		this.model = model;

		final MsCryptographicProvider provider = model.provider.getOld();
		if (!DbUtils.defaultId.equals(model.provider.getOld().getId())) {
			isAlterMode = true;
			fileTextField.setText(provider.getFilePath());
			nameTextField.setText(provider.getName());

			nameLabel.setEnabled(false);
			nameTextField.setEnabled(false);

			setTitle("Alter Cryptographic Provider " + provider.getName());
		} else {
			setTitle("Create Cryptographic Provider");
		}
	}

	private boolean isAlterMode;
	@Override
	public boolean isAlterMode() {
		return isAlterMode;
	}

	@NotNull
	@Override
	public ScriptGeneratorBase<MsCryptographicProviderModel> getScriptGenerator() {
		return CryptographicProviderGenerator.INSTANCE;
	}

	@Override
	public void activateSqlPreview(Function3<? super JPanel, ? super List<? extends JPanel>, ? super MsSqlScriptState, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel), null);
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "CryptographicProviderDialog";
	}

	@Nullable
	@Override
	public String getModelHelpId() {
		return "ru.coding4fun.intellij.database.help.security.crypto.provider";
	}
}