package ru.coding4fun.intellij.database.ui.form.security;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.intellij.database.data.property.DbNull;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.security.CertificateGenerator;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.property.security.MsCertificate;
import ru.coding4fun.intellij.database.model.property.security.MsCertificateModel;
import ru.coding4fun.intellij.database.ui.DialogUtilsKt;
import ru.coding4fun.intellij.database.ui.JComboBoxUtilKt;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.UiDependencyManager;
import ru.coding4fun.intellij.database.ui.form.UiDependencyRule;
import ru.coding4fun.intellij.database.ui.form.state.ComboBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.StateChanger;
import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class CertificateDialog extends JDialog implements ModelDialog<MsCertificateModel> {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField nameTextField;
	private JTextField authorizationTextField;
	private JRadioButton existingKeyRadioButton;
	private JTextField asnTextField;
	private JTextField privateKeyPathTextField;
	private JTextField privateKeyBitsTextField;
	private JTextField assemblyPathTextField;
	private JTextField encryptedTextField;
	private JTextField decryptedTextField;
	private JRadioButton generateNewKeyRadioButton;
	private JTextField subjectTextField;
	private JTextField passwordTextField;
	private JTextField expirateDateTextField;
	private JTextField startDateTextField;
	private JLabel fileLabel;
	private JLabel assemblyPathLabel;
	private JLabel asnLabel;
	private JLabel privateKeyPathLabel;
	private JLabel privateKeyBitsLabel;
	private JLabel passwordEncryptedLabel;
	private JLabel passwordDecryptedLabel;
	private JLabel subjectLabel;
	private JLabel passwordLabel;
	private JLabel dateLabel;
	private JLabel expirationLabel;
	private JLabel startLabel;
	private JLabel existingPasswordLabel;
	private JLabel privateKeyLabel;
	private JTextField assemblyNameTextField;
	private JLabel assemblyNameLabel;
	private JCheckBox beginDialogCheckBox;
	private JPanel generalPanel;
	private JPanel sqlPreviewPanel;
	private JComboBox<BasicIdentity> dbComboBox;

	public CertificateDialog() {
		this.setContentPane(contentPane);
		registerRules();
	}

	private Boolean isAssemblyNameNotSelected() {
		return !assemblyNameTextField.isEnabled() || assemblyNameTextField.getText().isEmpty();
	}

	private Boolean isAssemblyPathNotSelected() {
		return !assemblyPathTextField.isEnabled() || assemblyPathTextField.getText().isEmpty();
	}

	private Boolean isAsnNotSelected() {
		return !asnTextField.isEnabled() || asnTextField.getText().isEmpty();
	}

	private Boolean isPathNotSelected() {
		return !privateKeyPathTextField.isEnabled() || privateKeyPathTextField.getText().isEmpty();
	}

	private Boolean isBitsNotSelected() {
		return !privateKeyBitsTextField.isEnabled() || privateKeyBitsTextField.getText().isEmpty();
	}

	private boolean isPrivateKeyAvailable() {
		return TextFieldGetter.INSTANCE.apply(assemblyPathTextField) || TextFieldGetter.INSTANCE.apply(asnTextField);
	}

	private void registerRules() {
		//region File
		UiDependencyRule assemblyNameRule = new UiDependencyRule(assemblyNameLabel, assemblyNameTextField)
				.dependOn(assemblyPathTextField, asnTextField, existingKeyRadioButton, generateNewKeyRadioButton)
				.must(this::isAssemblyPathNotSelected, this::isAsnNotSelected, existingKeyRadioButton::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule assemblyPathRule = new UiDependencyRule(assemblyPathLabel, assemblyPathTextField)
				.dependOn(assemblyNameTextField, asnTextField, existingKeyRadioButton, generateNewKeyRadioButton)
				.must(this::isAssemblyNameNotSelected, this::isAsnNotSelected, existingKeyRadioButton::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule asnRule = new UiDependencyRule(asnLabel, asnTextField)
				.dependOn(assemblyPathTextField, assemblyNameTextField, existingKeyRadioButton, generateNewKeyRadioButton)
				.must(this::isAssemblyNameNotSelected, this::isAssemblyPathNotSelected, existingKeyRadioButton::isSelected)
				.setStateChanger(StateChanger::enable);
		//endregion

		//region Private key
		UiDependencyRule keyPathRule = new UiDependencyRule(privateKeyPathLabel, privateKeyPathTextField)
				.dependOn(privateKeyBitsTextField, existingKeyRadioButton, generateNewKeyRadioButton,
						assemblyNameTextField, assemblyPathTextField, asnTextField)
				.must(this::isPrivateKeyAvailable, this::isBitsNotSelected, existingKeyRadioButton::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule bitsRule = new UiDependencyRule(privateKeyBitsLabel, privateKeyBitsTextField)
				.dependOn(privateKeyPathTextField, existingKeyRadioButton, generateNewKeyRadioButton,
						assemblyNameTextField, assemblyPathTextField, asnTextField)
				.must(this::isPrivateKeyAvailable, this::isPathNotSelected, existingKeyRadioButton::isSelected)
				.setStateChanger(StateChanger::enable);
		//endregion

		UiDependencyRule existingKeyLRule = new UiDependencyRule(fileLabel, privateKeyLabel, passwordLabel,
				passwordLabel, encryptedTextField, decryptedTextField, passwordEncryptedLabel, passwordDecryptedLabel)
				.dependOn(generateNewKeyRadioButton, existingKeyRadioButton)
				.must(existingKeyRadioButton::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule generateNewKeyRules = new UiDependencyRule(subjectLabel, subjectTextField,
				passwordLabel, passwordTextField,
				dateLabel, expirationLabel, expirateDateTextField, startLabel, startDateTextField)
				.dependOn(generateNewKeyRadioButton, existingKeyRadioButton)
				.must(generateNewKeyRadioButton::isSelected)
				.setStateChanger(StateChanger::enable);


		UiDependencyManager.Companion.register(assemblyNameRule, assemblyPathRule, asnRule,
				keyPathRule, bitsRule, existingKeyLRule, generateNewKeyRules);
	}

	public MsCertificate getNewModel() {
		String password = TextFieldGetter.INSTANCE.getText(encryptedTextField);
		if (password == null) {
			password = TextFieldGetter.INSTANCE.getText(passwordTextField);
		}

		return new MsCertificate(
				DbNull.value,
				Objects.requireNonNull(TextFieldGetter.INSTANCE.getText(nameTextField)),
				TextFieldGetter.INSTANCE.getText(authorizationTextField),
				beginDialogCheckBox.isSelected(),
				TextFieldGetter.INSTANCE.getText(assemblyNameTextField),
				TextFieldGetter.INSTANCE.getText(assemblyPathTextField),
				TextFieldGetter.INSTANCE.getText(privateKeyPathTextField),
				TextFieldGetter.INSTANCE.getText(privateKeyBitsTextField),
				password,
				TextFieldGetter.INSTANCE.getText(decryptedTextField),
				TextFieldGetter.INSTANCE.getText(startDateTextField),
				TextFieldGetter.INSTANCE.getText(expirateDateTextField),
				TextFieldGetter.INSTANCE.getText(subjectTextField),
				TextFieldGetter.INSTANCE.getText(asnTextField),
				Objects.requireNonNull(ComboBoxGetter.INSTANCE.getText(dbComboBox))
		);
	}

	private void setOriginalModel(@NotNull MsCertificate msCertificate) {
		nameTextField.setText(msCertificate.getName());
		authorizationTextField.setText(msCertificate.getUserName());
		beginDialogCheckBox.setSelected(msCertificate.getBeginDialog());
		assemblyNameTextField.setText(msCertificate.getAssemblyName());
		assemblyPathTextField.setText(msCertificate.getAssemblyPath());
		asnTextField.setText(msCertificate.getAsn());
		privateKeyPathTextField.setText(msCertificate.getPrivateKeyPath());
		privateKeyBitsTextField.setText(msCertificate.getPrivateKeyBits());
		encryptedTextField.setText(msCertificate.getEncryptionPassword());
		decryptedTextField.setText(msCertificate.getDecryptionPassword());
		subjectTextField.setText(msCertificate.getSubject());
		expirateDateTextField.setText(msCertificate.getExpiryDate());
		startDateTextField.setText(msCertificate.getStartDate());
	}

	private MsCertificateModel model;
	@Override
	public MsCertificateModel getModel() {
		model.certificate.setNew(getNewModel());
		return model;
	}

	@Override
	public void setModel(MsCertificateModel model) {
		this.model = model;
		final MsCertificate certificate = model.certificate.getOld();
		String db = certificate == null ? null : certificate.getDb();
		JComboBoxUtilKt.synchronizeByName(dbComboBox, model.databases, db, null);
		if (certificate != null) {
			isAlterMode = true;
			DialogUtilsKt.disableAll(contentPane);
			setOriginalModel(certificate);
			setTitle("Alter Certificate " + certificate.getName());
		} else {
			setTitle("Create Certificate");
		}
	}

	private boolean isAlterMode;
	@Override
	public boolean isAlterMode() {
		return isAlterMode;
	}

	@NotNull
	@Override
	public ScriptGeneratorBase<MsCertificateModel> getScriptGenerator() {
		return CertificateGenerator.INSTANCE;
	}

	@Override
	public void activateSqlPreview(@NotNull Function2<? super JPanel, ? super List<? extends JPanel>, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel));
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "CertificateDialog";
	}

	@Nullable
	@Override
	public String getModelHelpId() {
		return "ru.coding4fun.intellij.database.help.security.certificate";
	}
}