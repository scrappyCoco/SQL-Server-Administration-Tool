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

package ru.coding4fun.intellij.database.generation.agent

import ru.coding4fun.intellij.database.data.property.DbUtils
import ru.coding4fun.intellij.database.extension.addCommaWithNewLineScope
import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.extension.commaWithNewLine
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.agent.proxy.MsProxyModel

object ProxyGenerator: ScriptGeneratorBase<MsProxyModel>() {
    override fun getCreatePart(model: MsProxyModel, scriptBuilder: StringBuilder): StringBuilder {
        val proxy = model.proxy.new ?: model.proxy.old

        scriptBuilder.appendJbLn("DECLARE @proxy_id INT;")
        scriptBuilder.appendJbLn("EXEC msdb.dbo.sp_add_proxy")
        scriptBuilder.append("  @proxy_name = ", DbUtils.getQuotedOrNull(proxy.name), ",").appendJbLn()
        scriptBuilder.append("  @enabled = ", if (proxy.enabled) "1" else "0", ",").appendJbLn()
        scriptBuilder.append("  @description = ", DbUtils.getQuotedOrNull(proxy.description), ",").appendJbLn()
        scriptBuilder.append("  @credential_name = ", DbUtils.getQuotedOrNull(proxy.credentialName), ",").appendJbLn()
        scriptBuilder.append("  @proxy_id = @proxy_id OUTPUT;").appendJbLn()

        return scriptBuilder
    }

    override fun getDropPart(model: MsProxyModel, scriptBuilder: StringBuilder): StringBuilder {
        scriptBuilder.appendJbLn("EXEC msdb.dbo.sp_delete_proxy")
        scriptBuilder.append("  @proxy_name = ", DbUtils.getQuotedOrNull(model.proxy.old.name), ";").appendJbLn()
        return scriptBuilder
    }

    override fun getAlterPart(model: MsProxyModel): String? {
        val scriptBuilder = StringBuilder()
        val newProxy = model.proxy.new!!

        scriptBuilder.appendJbLn("EXEC msdb.dbo.sp_update_proxy")
        scriptBuilder.append("  @proxy_name = ", model.proxy.old.name)

        scriptBuilder.addCommaWithNewLineScope(commaWithNewLine).also { paramScope ->
            paramScope.invokeIf (model.proxy.old.name != newProxy.name) {
                scriptBuilder.append("  @new_name = ", DbUtils.getQuotedOrNull(newProxy.name)).appendJbLn()
            }
            paramScope.invokeIf (model.proxy.old.credentialName != newProxy.credentialName) {
                scriptBuilder.append("  @credential_name = ", DbUtils.getQuotedOrNull(newProxy.credentialName)).appendJbLn()
            }
            paramScope.invokeIf (model.proxy.old.description != newProxy.description) {
                scriptBuilder.append("  @description = ", DbUtils.getQuotedOrNull(newProxy.description)).appendJbLn()
            }
            paramScope.invokeIf (model.proxy.old.enabled != newProxy.enabled) {
                scriptBuilder.append("  @enabled = ", if (newProxy.enabled) "1" else "0").appendJbLn()
            }
        }

        return scriptBuilder.toString()
    }
}