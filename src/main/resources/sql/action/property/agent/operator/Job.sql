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

SELECT id              = CAST(sysjobs.job_id AS VARCHAR(50)),
       name            = sysjobs.name,
       mailNotifyLevel = CAST(
               IIF(sysjobs.notify_email_operator_id = sysoperators.id, sysjobs.notify_level_email, 0) AS TINYINT),
       operatorId      = CAST(sysoperators.id AS VARCHAR(50))
FROM msdb.dbo.sysoperators
         CROSS JOIN msdb.dbo.sysjobs
UNION ALL
SELECT id              = CAST(sysjobs.job_id AS VARCHAR(50)),
       name            = sysjobs.name,
       mailNotifyLevel = CAST(0 AS TINYINT),
       operatorId      = '-1'
FROM msdb.dbo.sysjobs