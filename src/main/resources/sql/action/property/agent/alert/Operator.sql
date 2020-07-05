SELECT id         = CAST(sysoperators.id AS VARCHAR(10)),
       name       = sysoperators.name,
       isSelected = CAST(IIF(sysnotifications.alert_id IS NULL, 0, 1) AS BIT),
       alertId    = CAST(sysnotifications.alert_id AS VARCHAR(100))
FROM msdb.dbo.sysoperators
LEFT JOIN msdb.dbo.sysnotifications ON sysnotifications.operator_id = sysoperators.id
        AND sysnotifications.notification_method = 1 /*
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

-- e-mail