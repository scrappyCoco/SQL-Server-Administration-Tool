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

SELECT id                   = CAST(sysschedules.schedule_id AS VARCHAR(10)),
       name                 = sysschedules.name,
       enabled              = CAST(sysschedules.enabled AS BIT),
       freqType             = sysschedules.freq_type,
       freqInterval         = sysschedules.freq_interval,
       freqSubDayType       = sysschedules.freq_subday_type,
       freqSubDayInterval   = sysschedules.freq_subday_interval,
       freqRelativeInterval = sysschedules.freq_relative_interval,
       freqRecurrenceFactor = sysschedules.freq_recurrence_factor,
       activeStartDate      = sysschedules.active_start_date,
       activeEndDate        = sysschedules.active_end_date,
       activeStartTime      = sysschedules.active_start_time,
       activeEndTime        = sysschedules.active_end_time,
       ownerLoginName       = server_principals.name
FROM msdb.dbo.sysschedules
         LEFT JOIN master.sys.server_principals ON server_principals.sid = sysschedules.owner_sid
UNION ALL
SELECT id                   = '-1',
       name                 = 'Every hour',
       enabled              = CAST(1 AS BIT),
       freqType             = 4,
       freqInterval         = 1,
       freqSubDayType       = 8,
       freqSubDayInterval   = 1,
       freqRelativeInterval = 0,
       freqRecurrenceFactor = 1,
       activeStartDate      = 20191231,
       activeEndDate        = 21191231,
       activeStartTime      = 0,
       activeEndTime        = 235959,
       ownerLoginName       = null