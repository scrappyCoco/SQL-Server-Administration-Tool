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

package ru.coding4fun.intellij.database.ui.form.agent;

import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.coding4fun.intellij.database.data.property.DbUtils;
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase;
import ru.coding4fun.intellij.database.generation.agent.ProxyGenerator;
import ru.coding4fun.intellij.database.model.common.BasicIdentity;
import ru.coding4fun.intellij.database.model.property.agent.proxy.MsProxy;
import ru.coding4fun.intellij.database.model.property.agent.proxy.MsProxyModel;
import ru.coding4fun.intellij.database.ui.JComboBoxUtilKt;
import ru.coding4fun.intellij.database.ui.form.ModelDialog;
import ru.coding4fun.intellij.database.ui.form.MsSqlScriptState;
import ru.coding4fun.intellij.database.ui.form.state.CheckBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.ComboBoxGetter;
import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter;

import javax.swing.*;
import java.util.List;

public class ProxyDialog extends JDialog implements ModelDialog<MsProxyModel> {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField nameTextField;
    private JComboBox<BasicIdentity> credentialComboBox;
    private JTextArea descriptionTextArea;
    private JCheckBox enabledCheckBox;
    private JPanel sqlPreviewPanel;
    private JPanel generalPane;
    private MsProxyModel model;
    private boolean isAlterMode;

    public ProxyDialog() {
        this.setContentPane(contentPane);
    }

    @NotNull
    @Override
    public String getDialogId() {
        return "ProxyDialog";
    }

    @Override
    public MsProxyModel getModel() {
        model.proxy.setNew(new MsProxy(
                model.proxy.getOld().getId(),
                TextFieldGetter.INSTANCE.getTextOrCompute(nameTextField, () -> "My Proxy"),
                null,
                ComboBoxGetter.INSTANCE.getText(credentialComboBox),
                TextFieldGetter.INSTANCE.getText(descriptionTextArea),
                CheckBoxGetter.INSTANCE.apply(enabledCheckBox)
        ));
        return model;
    }

    @Override
    public void setModel(MsProxyModel model) {
        this.model = model;
        final MsProxy old = model.proxy.getOld();

        nameTextField.setText(old.getName());
        descriptionTextArea.setText(old.getDescription());
        enabledCheckBox.setSelected(old.getEnabled());
        JComboBoxUtilKt.synchronizeByName(credentialComboBox, model.credentials, old.getCredentialName(), null);

        if (DbUtils.defaultId.equals(old.getId())) {
            isAlterMode = false;
            setTitle("Create Proxy");
        } else {
            isAlterMode = true;
            setTitle("Alter Proxy " + old.getName());
        }
    }

    @Override
    public boolean isAlterMode() {
        return isAlterMode;
    }

    @NotNull
    @Override
    public ScriptGeneratorBase<MsProxyModel> getScriptGenerator() {
        return ProxyGenerator.INSTANCE;
    }

    @Nullable
    @Override
    public String getModelHelpId() {
        return "ru.coding4fun.intellij.database.help.agent.proxy";
    }

    @Override
    public void activateSqlPreview(@NotNull Function3<? super JPanel, ? super List<? extends JPanel>, ? super MsSqlScriptState, Unit> activateFun) {
        activateFun.invoke(sqlPreviewPanel, List.of(generalPane), null);
    }
}
