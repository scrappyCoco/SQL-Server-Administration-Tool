package ru.coding4fun.intellij.database.ui.form.security;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.security.CryptographicProviderGenerator;
import ru.coding4fun.intellij.database.model.property.security.MsCryptographicProvider;
import ru.coding4fun.intellij.database.model.property.security.MsCryptographicProviderModel;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;

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
		if (provider != null) {
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
	public void activateSqlPreview(@NotNull Function2<? super JPanel, ? super List<? extends JPanel>, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel));
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "CryptographicProviderDialog";
	}
}