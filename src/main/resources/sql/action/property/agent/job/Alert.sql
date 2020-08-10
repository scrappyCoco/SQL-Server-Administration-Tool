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

WITH Jobs AS (
    SELECT job_id
    FROM msdb.dbo.sysjobs
    UNION ALL
    SELECT 0x01
)
SELECT id         = CAST(id AS VARCHAR(10)),
       name       = sysalerts.name,
       isSelected = CAST(IIF(sysalerts.job_id = Jobs.job_id, 1, 0) AS BIT),
       isEnabled  = CAST(CASE
                             WHEN sysalerts.job_id = Jobs.job_id THEN 1
                             WHEN sysalerts.job_id = '00000000-0000-0000-0000-000000000000' THEN 1
                             ELSE 0
           END AS BIT),
       jobId      = IIF(Jobs.job_id <> 0x01, CAST(Jobs.job_id AS VARCHAR(50)), '-1')
FROM Jobs
CROSS JOIN msdb.dbo.sysalerts
ORDER BY name;