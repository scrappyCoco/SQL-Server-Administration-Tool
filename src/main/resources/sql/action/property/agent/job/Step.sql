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

SELECT id              = CAST(NEWID() AS VARCHAR(50)),
       name            = sysjobsteps.step_name,
       number          = sysjobsteps.step_id,
       type            = sysjobsteps.subsystem,
       onSuccessAction = sysjobsteps.on_success_action,
       onFailureAction = sysjobsteps.on_fail_action,
       command         = sysjobsteps.command,
       dbName          = sysjobsteps.database_name,
       proxyName       = sysproxies.name,
       retryAttempts   = sysjobsteps.retry_attempts,
       retryInterval   = sysjobsteps.retry_interval,
       appendFile      = CAST(CASE
                                  WHEN sysjobsteps.output_file_name IS NULL THEN 0
                                  WHEN sysjobsteps.flags & 2 = 2 THEN 0
                                  ELSE 1
           END AS BIT),
       overrideFile    = CAST(CASE
                                  WHEN sysjobsteps.output_file_name IS NULL THEN 0
                                  WHEN sysjobsteps.flags & 2 = 2 THEN 1
                                  ELSE 0
           END AS BIT),
       overrideFile    = CAST(IIF(sysjobsteps.flags & 2 = 2, 1, 0) AS BIT),
       appendTable     = CAST(IIF(sysjobsteps.flags & 8 = 8, 0, 1) AS BIT),
       overrideTable   = CAST(IIF(sysjobsteps.flags & 16 = 16, 1, 0) AS BIT),
       stepHistory     = CAST(IIF(sysjobsteps.flags & 4 = 4, 1, 0) AS BIT),
       jobHistory      = CAST(IIF(sysjobsteps.flags & 32 = 32, 1, 0) AS BIT),
       abortEvent      = CAST(IIF(sysjobsteps.flags & 64 = 64, 1, 0) AS BIT),
       jobId           = CAST(sysjobsteps.job_id AS VARCHAR(100))
FROM msdb.dbo.sysjobsteps
LEFT JOIN msdb.dbo.sysproxies ON sysproxies.proxy_id = sysjobsteps.proxy_id;