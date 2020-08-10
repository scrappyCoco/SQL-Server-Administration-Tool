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

import com.intellij.ui.CheckBoxList;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.intellij.database.data.property.DbUtils;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.security.ServerAuditSpecificationGenerator;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditSpecModel;
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditSpecification;
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditSpecificationAction;
import ru.coding4fun.intellij.database.ui.JComboBoxUtilKt;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.MsSqlScriptState;
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
				DbUtils.defaultId,
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
		final MsServerAuditSpecification spec = model.spec.getOld();

		if (DbUtils.defaultId.equals(spec.getId())) {
			setTitle("Create Server Audit Specification");
		} else {
			isAlterMode = true;
			nameLabel.setEnabled(false);
			nameTextField.setEnabled(false);
			setTitle("Alter Server Audit Specification " + spec.getName());
		}

		nameTextField.setText(spec.getName());
		String auditName = spec.getAuditName();
		isEnabledCheckBox.setSelected(spec.isEnabled());

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
	public void activateSqlPreview(Function3<? super JPanel, ? super List<? extends JPanel>, ? super MsSqlScriptState, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel), null);
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "ServerAuditSpecificationDialog";
	}

	@Nullable
	@Override
	public String getModelHelpId() {
		return "ru.coding4fun.intellij.database.help.security.server.audit.specification";
	}
}
