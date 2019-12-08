DECLARE @auditGuid UNIQUEIDENTIFIER = '??auditGuid??';

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
FROM sys.server_audits
LEFT JOIN sys.server_file_audits ON server_file_audits.audit_id = server_audits.audit_id
WHERE @auditGuid = server_audits.audit_guid;