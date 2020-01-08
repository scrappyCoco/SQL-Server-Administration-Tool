package ru.coding4fun.intellij.database.ui.form.agent.tool;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.agent.FindJobGenerator;
import ru.coding4fun.intellij.database.model.tool.MsFindJob;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter;

import javax.swing.*;
import java.util.List;

public class FindJobDialog extends JDialog implements ModelDialog<MsFindJob> {
	private JPanel contentPane;
	private JTextField dbTextField;
	private JTextField commandTextField;
	private JPanel generalPanel;
	private JPanel sqlPreviewPanel;

	public FindJobDialog() {
		this.setContentPane(contentPane);
		setTitle("Find Job");
	}

	@Override
	public MsFindJob getModel() {
		return new MsFindJob(
				TextFieldGetter.INSTANCE.getTextOrCompute(dbTextField, () -> "%"),
				TextFieldGetter.INSTANCE.getTextOrCompute(commandTextField, () -> "%")
		);
	}

	@Override
	public void setModel(MsFindJob msFindJob) {
	}

	@Override
	public boolean isAlterMode() {
		return false;
	}

	@NotNull
	@Override
	public ScriptGeneratorBase<MsFindJob> getScriptGenerator() {
		return FindJobGenerator.INSTANCE;
	}

	@Override
	public void activateSqlPreview(@NotNull Function2<? super JPanel, ? super List<? extends JPanel>, Unit> activateFun) {
		activateFun.invoke(sqlPreviewPanel, List.of(generalPanel));
	}

	@NotNull
	@Override
	public String getDialogId() {
		return "FindJobDialog";
	}

	@Nullable
	@Override
	public String getModelHelpId() {
		return null;
	}
}
