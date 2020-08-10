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

SELECT id                = CAST(server_audits.audit_guid AS VARCHAR(50)),
       name              = server_audits.name,
       isEnabled         = server_audits.is_state_enabled,
       queueDelay        = server_audits.queue_delay,
       onAuditLogFailure = REPLACE(server_audits.on_failure_desc, ' ', '_'),
       auditDestination  = REPLACE(server_audits.type_desc, ' ', '_'),
       filePath          = server_file_audits.log_file_path,
       maxSize           = server_file_audits.max_file_size,
       maxSizeUnit       = 'MB',
       maxRolloverFiles  = server_file_audits.max_rollover_files,
       maxFiles          = server_file_audits.max_files,
       reserveDiskSpace  = server_file_audits.reserve_disk_space
FROM master.sys.server_audits
LEFT JOIN master.sys.server_file_audits ON server_file_audits.audit_id = server_audits.audit_id
UNION ALL
SELECT id                = '-1',
       name              = 'My Audit',
       isEnabled         = CAST(1 AS BIT),
       queueDelay        = 1000,
       onAuditLogFailure = 'CONTINUE',
       auditDestination  = 'FILE',
       filePath          = 'C:\SqlServer\MyAudit',
       maxSize           = CAST(100 AS BIGINT),
       maxSizeUnit       = 'MB',
       maxRolloverFiles  = 10,
       maxFiles          = 10,
       reserveDiskSpace  = CAST(0 AS BIT)