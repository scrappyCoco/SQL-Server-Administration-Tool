package ru.coding4fun.intellij.database.ui.form.security;

import com.intellij.ui.CheckBoxList;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.security.LoginGenerator;
import ru.coding4fun.intellij.database.generation.security.PrincipalKind;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.common.BuiltinPermission;
import ru.coding4fun.intellij.database.model.property.security.login.*;
import ru.coding4fun.intellij.database.model.property.security.role.RoleMember;
import ru.coding4fun.intellij.database.ui.JComboBoxUtilKt;
import ru.coding4fun.intellij.database.ui.form.DialogHelper;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.UiDependencyManager;
import ru.coding4fun.intellij.database.ui.form.UiDependencyRule;
import ru.coding4fun.intellij.database.ui.form.common.CheckBoxListModTracker;
import ru.coding4fun.intellij.database.ui.form.common.ModificationTracker;
import ru.coding4fun.intellij.database.ui.form.security.login.database.DatabaseMediator;
import ru.coding4fun.intellij.database.ui.form.security.login.securable.SecurableAdapter;
import ru.coding4fun.intellij.database.ui.form.security.login.securable.SecurableMediator;
import ru.coding4fun.intellij.database.ui.form.state.CheckBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.RadioButtonGetter;
import ru.coding4fun.intellij.database.ui.form.state.StateChanger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class LoginDialog extends JDialog implements ModelDialog<MsLoginModel> {

	//<editor-fold desc="UI fields">
	private JPanel contentPane;
	private JTextField loginNameTextField;
	private JLabel loginNameLabel;
	private JRadioButton windowsAuthenticationRadioButton;
	private JRadioButton sqlServerAuthenticationRadioButton;
	private JPasswordField passwordField;
	private JCheckBox enforcePasswordPolicyCheckBox;
	private JCheckBox enforcePasswordExpirationCheckBox;
	private JCheckBox userMustChangePasswordCheckBox;
	private JRadioButton mappedToCertificateRadioButton;
	private JRadioButton mappedToAsymmetricKeyRadioButton;
	private JCheckBox mapToCredentialCheckBox;
	private JComboBox<BasicIdentity> credentialComboBox;
	private JComboBox<BasicIdentity> asymmetricKeyComboBox;
	private JComboBox<BasicIdentity> certificateComboBox;
	private JComboBox<BasicIdentity> defaultDatabaseComboBox;
	private JComboBox<BasicIdentity> defaultLanguageComboBox;
	private JLabel passwordLabel;
	private JCheckBox defaultDatabaseCheckBox;
	private JCheckBox defaultLanguageCheckBox;
	private JTabbedPane tabbedPane;
	private CheckBoxList<MsDatabaseRoleMembership> databaseRoleMembershipList;
	private JCheckBox showAllSecurablesCheckBox;
	private JCheckBox loginEnabledCheckBox;
	private JPanel sqlPreviewPanel;
	private JPanel generalPanel;
	private JPanel serverRolePanel;
	private JPanel userMappingPanel;
	private JPanel securablePanel;
	private JScrollPane serverRolesScrollPane;
	private CheckBoxList<RoleMember> serverRoleList;
	private JScrollPane userMapPane;
	private JScrollPane securableScrollPane;
	private JScrollPane permissionList;
	//</editor-fold>

	//<editor-fold desc="Local fields">
	private ModificationTracker<RoleMember> serverRoleMembershipModificationTracker;
	private ModificationTracker<MsServerPermission> securableModificationTracker;
	private ModificationTracker<MsDatabaseOfLogin> dbModTracker;
	private ModificationTracker<MsDatabaseRoleMembership> dbRoleModTracker;
	//</editor-fold>
	private List<UiDependencyRule> rules;
	private MsLoginModel model;
	private boolean isAlterModel;

	public LoginDialog() {
		this.setContentPane(contentPane);
		registerRules();
	}

	@SuppressWarnings("DuplicatedCode")
	private void registerRules() {
		UiDependencyRule sqlServerAuthRule = new UiDependencyRule(passwordLabel, passwordField,
				enforcePasswordPolicyCheckBox, enforcePasswordExpirationCheckBox)
				.dependOn(windowsAuthenticationRadioButton, sqlServerAuthenticationRadioButton,
						mappedToCertificateRadioButton, mappedToAsymmetricKeyRadioButton)
				.must(() -> RadioButtonGetter.INSTANCE.apply(sqlServerAuthenticationRadioButton))
				.setStateChanger(StateChanger::enable);

		UiDependencyRule mustChangePasswordRule = new UiDependencyRule(userMustChangePasswordCheckBox)
				.dependOn(windowsAuthenticationRadioButton, sqlServerAuthenticationRadioButton,
						mappedToCertificateRadioButton, mappedToAsymmetricKeyRadioButton)
				.must(() -> RadioButtonGetter.INSTANCE.apply(sqlServerAuthenticationRadioButton))
				.setStateChanger(StateChanger::enable);


		UiDependencyRule passwordPolicyRule = new UiDependencyRule(enforcePasswordPolicyCheckBox)
				.dependOn(windowsAuthenticationRadioButton, sqlServerAuthenticationRadioButton,
						mappedToCertificateRadioButton, mappedToAsymmetricKeyRadioButton)
				.must(() -> RadioButtonGetter.INSTANCE.apply(sqlServerAuthenticationRadioButton))
				.setStateChanger(StateChanger::enable);

		UiDependencyRule passwordExpirationRule = new UiDependencyRule(enforcePasswordExpirationCheckBox)
				.dependOn(windowsAuthenticationRadioButton, sqlServerAuthenticationRadioButton,
						mappedToCertificateRadioButton, mappedToAsymmetricKeyRadioButton, enforcePasswordPolicyCheckBox)
				.must(() -> CheckBoxGetter.INSTANCE.apply(enforcePasswordPolicyCheckBox))
				.setStateChanger(StateChanger::enable);

		UiDependencyRule certificateRule = new UiDependencyRule(certificateComboBox)
				.dependOn(windowsAuthenticationRadioButton, sqlServerAuthenticationRadioButton,
						mappedToCertificateRadioButton, mappedToAsymmetricKeyRadioButton)
				.must(() -> RadioButtonGetter.INSTANCE.apply(mappedToCertificateRadioButton))
				.setStateChanger(StateChanger::enable);

		UiDependencyRule asymmetricKeyRule = new UiDependencyRule(asymmetricKeyComboBox)
				.dependOn(windowsAuthenticationRadioButton, sqlServerAuthenticationRadioButton,
						mappedToCertificateRadioButton, mappedToAsymmetricKeyRadioButton)
				.must(() -> RadioButtonGetter.INSTANCE.apply(mappedToAsymmetricKeyRadioButton))
				.setStateChanger(StateChanger::enable);

		UiDependencyRule credentialRule = new UiDependencyRule(credentialComboBox)
				.dependOn(mapToCredentialCheckBox)
				.must(mapToCredentialCheckBox::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule databaseRule = new UiDependencyRule(defaultDatabaseComboBox)
				.dependOn(defaultDatabaseCheckBox)
				.must(defaultDatabaseCheckBox::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule languageRule = new UiDependencyRule(defaultLanguageComboBox)
				.dependOn(defaultLanguageCheckBox)
				.must(defaultLanguageCheckBox::isSelected)
				.setStateChanger(StateChanger::enable);

		UiDependencyRule windowsOptionRule = new UiDependencyRule(
				defaultDatabaseCheckBox, defaultDatabaseComboBox,
				defaultLanguageCheckBox, defaultLanguageComboBox)
				.dependOn(sqlServerAuthenticationRadioButton, windowsAuthenticationRadioButton,
						mappedToCertificateRadioButton, mappedToAsymmetricKeyRadioButton)
				.must(this::isWindowsOption)
				.setStateChanger(StateChanger::enable);

		rules = UiDependencyManager.Companion.register(sqlServerAuthRule, certificateRule,
				asymmetricKeyRule, credentialRule,
				databaseRule, languageRule, passwordPolicyRule, passwordExpirationRule, mustChangePasswordRule,
				windowsOptionRule);

		for (UiDependencyRule rule : rules) rule.apply();
	}

	private boolean isWindowsOption() {
		return windowsAuthenticationRadioButton.isSelected() || sqlServerAuthenticationRadioButton.isSelected();
	}

	private void bindTab1General(@NotNull MsLoginModel model) {
		this.model = model;
		final List<BasicIdentity> credentials = model.credentials;
		final MsLogin oldLogin = model.login.getOld();
		String modelCredential = oldLogin == null ? null : oldLogin.getCredential();
		JComboBoxUtilKt.synchronizeByName(credentialComboBox, credentials, modelCredential, mapToCredentialCheckBox);

		final List<BasicIdentity> databases = model.databases;
		String modelDefaultDatabase = oldLogin == null ? null : oldLogin.getDefaultDatabase();
		JComboBoxUtilKt.synchronizeByName(defaultDatabaseComboBox, databases, modelDefaultDatabase, defaultDatabaseCheckBox);

		final List<BasicIdentity> languages = model.languages;
		String modelDefaultLanguage = oldLogin == null ? null : oldLogin.getDefaultLanguage();
		JComboBoxUtilKt.synchronizeByName(defaultLanguageComboBox, languages, modelDefaultLanguage, defaultLanguageCheckBox);

		final List<BasicIdentity> certificates = model.getCertificates();
		JComboBoxUtilKt.addAll(certificateComboBox, certificates);

		final List<BasicIdentity> asymmetricKeys = model.getAsymmetricKeys();
		JComboBoxUtilKt.addAll(asymmetricKeyComboBox, asymmetricKeys);

		if (model.login.getOld() != null) {
			JComboBoxUtilKt.synchronizeById(certificateComboBox, certificates, getNewModel().getCertificate(), mappedToCertificateRadioButton);
			JComboBoxUtilKt.synchronizeById(asymmetricKeyComboBox, asymmetricKeys, getNewModel().getAsymmetricKey(), mappedToAsymmetricKeyRadioButton);

			windowsAuthenticationRadioButton.setEnabled(false);
			sqlServerAuthenticationRadioButton.setEnabled(false);
			mappedToCertificateRadioButton.setEnabled(false);
			mappedToAsymmetricKeyRadioButton.setEnabled(false);
			loginNameTextField.setEnabled(false);
			loginNameLabel.setEnabled(false);
		} else {
			defaultDatabaseCheckBox.setEnabled(true);
			defaultLanguageCheckBox.setEnabled(true);
		}


		for (UiDependencyRule rule : rules) {
			rule.apply();
		}
	}

	private void bindTab2ServerRole(@NotNull MsLoginModel model) {
		serverRoleMembershipModificationTracker = new CheckBoxListModTracker<>(serverRolesScrollPane, model.serverRoles);
	}

	private void bindTab3Database(@NotNull MsLoginModel model) {
		final DatabaseMediator databaseMediator = new DatabaseMediator(userMapPane, model);
		dbModTracker = databaseMediator.getDbModTracker();
		dbRoleModTracker = databaseMediator.getDbRoleModTracker();
	}

	private void bindTab4Securable(@NotNull MsLoginModel model) {
		securableModificationTracker = new SecurableMediator(
				securableScrollPane,
				new SecurableAdapter() {
					@NotNull
					@Override
					public List<BuiltinPermission> getBuiltInPermission() {
						return model.builtInPermission;
					}

					@NotNull
					@Override
					public List<MsServerPermission> getServerPermissions() {
						return model.serverPermissions;
					}

					@NotNull
					@Override
					public List<MsSecurable> getSecurables() {
						return model.securables;
					}

					@Override
					public boolean isAlterMode() {
						return isAlterModel;
					}
				}
		);
	}

	private void selectRadioButton() {
		final MsLogin login = model.login.getOld();
		if (login.getPrincipalKind().equals(PrincipalKind.SQL_LOGIN.toString()))
			sqlServerAuthenticationRadioButton.setSelected(true);
		else if (login.getPrincipalKind().equals(PrincipalKind.WINDOWS_LOGIN.toString()))
			windowsAuthenticationRadioButton.setSelected(true);
		else if (login.getPrincipalKind().equals(PrincipalKind.CERTIFICATE_MAPPED_LOGIN.toString()))
			mappedToCertificateRadioButton.setSelected(true);
		else if (login.getPrincipalKind().equals(PrincipalKind.ASYMMETRIC_KEY_MAPPED_LOGIN.toString()))
			mappedToAsymmetricKeyRadioButton.setSelected(true);
	}

	@NotNull
	@Contract(" -> new")
	private MsLogin getNewModel() {
		return new MsLogin(
				loginNameTextField.getText(),
				getPrincipalKind(),
				DialogHelper.INSTANCE.getSelectedName(defaultDatabaseComboBox),
				DialogHelper.INSTANCE.getSelectedName(defaultLanguageComboBox),
				DialogHelper.INSTANCE.getString(passwordField),
				null,
				null,
				DialogHelper.INSTANCE.getBoolean(enforcePasswordPolicyCheckBox),
				DialogHelper.INSTANCE.getBoolean(enforcePasswordExpirationCheckBox),
				false,
				!DialogHelper.INSTANCE.getBoolean(loginEnabledCheckBox),
				DialogHelper.INSTANCE.getBoolean(userMustChangePasswordCheckBox),
				DialogHelper.INSTANCE.getSelectedName(credentialComboBox),
				DialogHelper.INSTANCE.getSelectedName(certificateComboBox),
				DialogHelper.INSTANCE.getSelectedName(asymmetricKeyComboBox)
		);
	}

	private @NotNull
	String getPrincipalKind() {
		ArrayList<Pair<JRadioButton, PrincipalKind>> radioButtons = new ArrayList<>();

		radioButtons.add(new Pair<>(windowsAuthenticationRadioButton, PrincipalKind.WINDOWS_LOGIN));
		radioButtons.add(new Pair<>(sqlServerAuthenticationRadioButton, PrincipalKind.SQL_LOGIN));
		radioButtons.add(new Pair<>(mappedToCertificateRadioButton, PrincipalKind.CERTIFICATE_MAPPED_LOGIN));
		radioButtons.add(new Pair<>(mappedToAsymmetricKeyRadioButton, PrincipalKind.ASYMMETRIC_KEY_MAPPED_LOGIN));

		for (Pair<JRadioButton, PrincipalKind> radioButton : radioButtons) {
			if (radioButton.getFirst().isSelected()) {
				return radioButton.getSecond().toString();
			}
		}

		throw new NotImplementedException("Can't get principal kind.");
	}

	@Override
	public MsLoginModel getModel() {
		model.login.setNew(getNewModel());
		model.setDbModifications(dbModTracker.getModifications());
		model.setDbRoleModifications(dbRoleModTracker.getModifications());
		model.setMemberModifications(serverRoleMembershipModificationTracker.getModifications());
		model.setServerPermissionModifications(securableModificationTracker.getModifications());
		return model;
	}

	@Override
	public void setModel(MsLoginModel model) {
		this.model = model;
		final MsLogin oldLogin = model.login.getOld();
		if (oldLogin != null) {
			isAlterModel = true;
			selectRadioButton();
			loginNameTextField.setText(oldLogin.getName());
			setTitle("Alter Login " + oldLogin.getName());
		} else {
			setTitle("Create Login");
		}

		bindTab1General(model);
		bindTab2ServerRole(model);
		bindTab3Database(model);
		bindTab4Securable(model);

		for (UiDependencyRule rule : rules) rule.apply();
	}

	@Override
	public boolean isAlterMode() {
		return isAlterModel;
	}

	@NotNull
	@Override
	public ScriptGeneratorBase<MsLoginModel> getScriptGenerator() {
		return LoginGenerator.INSTANCE;
	}

	@Override
	public void activateSqlPreview(@NotNull Function2<? super JPanel, ? super List<? extends JPanel>, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel, serverRolePanel, userMappingPanel, securablePanel));
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "LoginDialog";
	}
}