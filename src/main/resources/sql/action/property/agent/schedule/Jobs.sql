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

SELECT id         = CAST(sysjobs.job_id AS VARCHAR(50)),
       name       = sysjobs.name,
       isSelected = CAST(IIF(sysjobschedules.job_id IS NOT NULL, 1, 0) AS BIT),
       kind       = 'JOB',
       scheduleId = CAST(sysschedules.schedule_id as VARCHAR(10))
FROM msdb.dbo.sysschedules
         CROSS JOIN msdb.dbo.sysjobs
         LEFT JOIN msdb.dbo.sysjobschedules ON sysjobs.job_id = sysjobschedules.job_id
    AND sysjobschedules.schedule_id = sysschedules.schedule_id
UNION ALL
SELECT id         = CAST(sysjobs.job_id AS VARCHAR(50)),
       name       = sysjobs.name,
       isSelected = CAST(0 AS BIT),
       kind       = 'JOB',
       scheduleId = '-1'
FROM msdb.dbo.sysjobs;