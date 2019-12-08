package ru.coding4fun.intellij.database.ui.form.agent;

import com.intellij.ui.CheckBoxList;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.agent.OperatorGenerator;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.property.agent.MsOperator;
import ru.coding4fun.intellij.database.model.property.agent.operator.MsOperatorAlert;
import ru.coding4fun.intellij.database.model.property.agent.operator.MsOperatorJob;
import ru.coding4fun.intellij.database.model.property.agent.operator.MsOperatorModel;
import ru.coding4fun.intellij.database.ui.DialogUtilsKt;
import ru.coding4fun.intellij.database.ui.JComboBoxUtilKt;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.common.ModificationTracker;
import ru.coding4fun.intellij.database.ui.form.state.CheckBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.ComboBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter;

import javax.swing.*;
import java.util.List;

public class OperatorDialog extends JDialog implements ModelDialog<MsOperatorModel> {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JLabel nameLabel;
	private JTextField nameTextField;
	private JCheckBox enabledCheckBox;
	private JTextField emailTextField;
	private JLabel emailLabel;
	private JTable jobTable;
	private JLabel alertsLabel;
	private JLabel jobsLabel;
	private JComboBox<BasicIdentity> categoryComboBox;
	private JPanel sqlPreviewPanel;
	private JPanel generalPanel;
	private JPanel notificationsPanel;
	private CheckBoxList<MsOperatorAlert> alertList;

	private ModificationTracker<MsOperatorAlert> alertsModTracker;
	private ModificationTracker<MsOperatorJob> jobModTracker;

	public OperatorDialog() {
		this.setContentPane(contentPane);
		registerRules();
	}

	private void registerRules() {

	}

	public MsOperator getNewModel() {
		final MsOperator operator = model.operator.getOld();
		return new MsOperator(operator == null ? "" : operator.getId(),
				TextFieldGetter.INSTANCE.getTextOrCompute(nameTextField, () -> ""),
				CheckBoxGetter.INSTANCE.apply(enabledCheckBox),
				TextFieldGetter.INSTANCE.getTextOrCompute(emailTextField, () -> ""),
				ComboBoxGetter.INSTANCE.getText(categoryComboBox)
		);
	}

	private void bindTab1General() {
		String categoryName = null;
		final MsOperator operator = model.operator.getOld();
		if (operator != null) {
			nameTextField.setText(operator.getName());
			emailTextField.setText(operator.getEMail());
			enabledCheckBox.setSelected(operator.isEnabled());
			categoryName = operator.getCategoryName();
		}
		final List<BasicIdentity> categories = model.getOperatorCategories();
		JComboBoxUtilKt.synchronizeByName(categoryComboBox, categories, categoryName, null);
	}

	private void bindTab2Notifications() {
		DialogUtilsKt.disableAll(notificationsPanel);
//		final OperatorJobTableModel tableModel = new OperatorJobTableModel();
//		tableModel.setRows(model.jobs);
//		jobTable.setModel(tableModel);
//		jobModTracker = new TableModificationTracker<>(tableModel);
//		alertsModTracker = new CheckBoxListModTracker<>(alertList, model.getAlerts());
	}

	@Override
	public MsOperatorModel getModel() {
		model.operator.setNew(getNewModel());
		model.setAlertModifications(alertsModTracker.getModifications());
		model.setJobModifications(jobModTracker.getModifications());
		return model;
	}

	private MsOperatorModel model;

	@Override
	public void setModel(MsOperatorModel model) {
		this.model = model;
		final MsOperator operator = model.operator.getOld();
		if (operator != null) {
			isAlterMode = true;
			setTitle("Alter Operator " + operator.getName());
		} else {
			setTitle("Create Operator");
		}

		bindTab1General();
		bindTab2Notifications();
	}

	private boolean isAlterMode;

	@Override
	public boolean isAlterMode() {
		return isAlterMode;
	}

	@NotNull
	@Override
	public ScriptGeneratorBase<MsOperatorModel> getScriptGenerator() {
		return OperatorGenerator.INSTANCE;
	}

	@Override
	public void activateSqlPreview(@NotNull Function2<? super JPanel, ? super List<? extends JPanel>, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel, notificationsPanel));
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "OperatorDialog";
	}
}
