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

SELECT id         = CAST(sysalerts.id AS VARCHAR(10)),
       name       = sysalerts.name,
       sendToMail = CAST(IIF(sysnotifications.alert_id IS NOT NULL, 1, 0) AS BIT),
       operatorId = CAST(sysoperators.id AS VARCHAR(10))
FROM msdb.dbo.sysoperators
         CROSS APPLY msdb.dbo.sysalerts
         LEFT JOIN msdb.dbo.sysnotifications ON sysnotifications.alert_id = sysalerts.id
    AND sysnotifications.operator_id = sysoperators.id
UNION ALL
SELECT id         = CAST(sysalerts.id AS VARCHAR(10)),
       name       = sysalerts.name,
       sendToMail = CAST(0 AS BIT),
       operatorId = '-1'
FROM msdb.dbo.sysalerts