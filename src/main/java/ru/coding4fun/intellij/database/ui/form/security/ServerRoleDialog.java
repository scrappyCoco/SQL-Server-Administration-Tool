package ru.coding4fun.intellij.database.ui.form.security;

import com.intellij.ui.CheckBoxList;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.security.ServerRoleGenerator;
import ru.coding4fun.intellij.database.model.common.BuiltinPermission;
import ru.coding4fun.intellij.database.model.property.security.MsServerRoleModel;
import ru.coding4fun.intellij.database.model.property.security.login.MsSecurable;
import ru.coding4fun.intellij.database.model.property.security.login.MsServerPermission;
import ru.coding4fun.intellij.database.model.property.security.role.RoleMember;
import ru.coding4fun.intellij.database.model.property.security.role.ServerRole;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.common.CheckBoxListModTracker;
import ru.coding4fun.intellij.database.ui.form.common.ModificationTracker;
import ru.coding4fun.intellij.database.ui.form.security.login.securable.SecurableAdapter;
import ru.coding4fun.intellij.database.ui.form.security.login.securable.SecurableMediator;
import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter;

import javax.swing.*;
import java.util.List;

public class ServerRoleDialog extends JDialog implements ModelDialog<MsServerRoleModel> {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField nameTextField;
	private JTextField authTextField;
	private JTabbedPane tabbedPane;
	private JCheckBox showAllSecurablesCheckBox;
	private JTable securablesTable;
	private JTable permissionTable;
	private JPanel sqlPreviewPanel;
	private JPanel generalPanel;
	private JPanel membersPanel;
	private JPanel membershipsPanel;
	private JPanel securablesPanel;
	private CheckBoxList<RoleMember> membersList;
	private CheckBoxList<RoleMember> membershipsList;
	private JLabel authLabel;
	private JScrollPane membersScrollPanel;
	private JScrollPane membershipScrollPane;
	private JScrollPane securableScrolPane;
	private JLabel nameLabel;

	private ModificationTracker<RoleMember> memberListModTracker;
	private ModificationTracker<RoleMember> membershipListModTracker;
	private ModificationTracker<MsServerPermission> serverPermissionModTracker;

	public ServerRoleDialog() {
		this.setContentPane(contentPane);
	}


	public ServerRole getNewModel() {
		return new ServerRole(model.role.getOld() == null ? "" : model.role.getOld().getId(),
				nameTextField.getText(),
				TextFieldGetter.INSTANCE.getText(authTextField));
	}


	private void bindTab4Securable() {
		serverPermissionModTracker = new SecurableMediator(
				securableScrolPane,
				new SecurableAdapter() {
					@Override
					public boolean isAlterMode() {
						return isAlterMode;
					}

					@NotNull
					@Override
					public List<MsSecurable> getSecurables() {
						return model.securables;
					}

					@NotNull
					@Override
					public List<MsServerPermission> getServerPermissions() {
						return model.serverPermissions;
					}

					@NotNull
					@Override
					public List<BuiltinPermission> getBuiltInPermission() {
						return model.builtin;
					}
				});
	}


	private MsServerRoleModel model;

	@Override
	public MsServerRoleModel getModel() {
		model.role.setNew(getNewModel());
		model.membershipModifications = membershipListModTracker.getModifications();
		model.memberModifications = memberListModTracker.getModifications();
		model.serverPermissionModifications = serverPermissionModTracker.getModifications();
		return model;
	}

	@Override
	public void setModel(MsServerRoleModel model) {
		this.model = model;

		memberListModTracker = new CheckBoxListModTracker<>(membersScrollPanel, model.members);
		membershipListModTracker = new CheckBoxListModTracker<>(membershipScrollPane, model.memberships);

		ServerRole oldModel = model.role.getOld();
		if (oldModel != null) {
			isAlterMode = true;
			final String name = oldModel.getName();
			nameTextField.setText(name);
			authLabel.setEnabled(false);
			authTextField.setEnabled(false);
			authTextField.setText(oldModel.getAuth());
			setTitle("Alter Server Role " + name);
		} else {
			setTitle("Create Server Role");
		}

		bindTab4Securable();
	}

	private boolean isAlterMode;

	@Override
	public boolean isAlterMode() {
		return isAlterMode;
	}

	@NotNull
	@Override
	public ScriptGeneratorBase<MsServerRoleModel> getScriptGenerator() {
		return ServerRoleGenerator.INSTANCE;
	}

	@Override
	public void activateSqlPreview(@NotNull Function2<? super JPanel, ? super List<? extends JPanel>, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel, membersPanel, membershipsPanel, securablesPanel));
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "ServerRoleDialog";
	}

	@Nullable
	@Override
	public String getModelHelpId() {
		return "ru.coding4fun.intellij.database.help.security.server.role";
	}
}