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

SELECT id                = CAST(sysjobs.job_id AS VARCHAR(50)),
       name              = sysjobs.name,
       isEnabled         = CAST(sysjobs.enabled AS BIT),
       description       = sysjobs.description,
       startStepId       = CAST(sysjobs.start_step_id AS VARCHAR(10)),
       categoryId        = CAST(syscategories.category_id AS VARCHAR(10)),
       categoryName      = syscategories.name,
       ownerName         = syslogins.name,
       dateCreated       = CAST(sysjobs.date_created AS VARCHAR(50)),
       lastModified      = CAST(sysjobs.date_modified AS VARCHAR(50)),
       lastExecuted      = (
                               SELECT TOP 1 CAST(sysjobhistory.run_date AS VARCHAR(20))
                               FROM msdb.dbo.sysjobhistory
                               WHERE sysjobhistory.job_id = sysjobs.job_id
                           ),
       eMailNotifyLevel  = CAST(NULLIF(sysjobs.notify_level_email, 0) AS TINYINT),
       eventLogLevel     = CAST(NULLIF(sysjobs.notify_level_eventlog, 0) AS TINYINT),
       eMailOperatorId   = CAST(NULLIF(sysjobs.notify_email_operator_id, 0) AS VARCHAR(10)),
       eMailOperatorName = sysoperators.name,
       deleteLevel       = CAST(NULLIF(sysjobs.delete_level, 0) AS TINYINT)
FROM msdb.dbo.sysjobs
LEFT JOIN sys.syslogins ON syslogins.sid = sysjobs.owner_sid
LEFT JOIN msdb.dbo.syscategories ON syscategories.category_id = sysjobs.category_id
LEFT JOIN msdb.dbo.sysoperators ON sysoperators.id = sysjobs.notify_email_operator_id
UNION ALL
SELECT id                = '-1',
       name              = 'My Job',
       isEnabled         = CAST(1 AS BIT),
       description       = 'My serious job.',
       startStepId       = '1',
       categoryId        = CAST(NULL AS VARCHAR(1)),
       categoryName      = CAST(NULL AS VARCHAR(1)),
       ownerName         = CAST(NULL AS VARCHAR(1)),
       dateCreated       = CAST(NULL AS VARCHAR(1)),
       lastModified      = CAST(NULL AS VARCHAR(1)),
       lastExecuted      = CAST(NULL AS VARCHAR(1)),
       eMailNotifyLevel  = CAST(0 AS TINYINT),
       eventLogLevel     = CAST(0 AS TINYINT),
       eMailOperatorId   = CAST(NULL AS VARCHAR(1)),
       eMailOperatorName = CAST(NULL AS VARCHAR(1)),
       deleteLevel       = CAST(0 AS TINYINT);