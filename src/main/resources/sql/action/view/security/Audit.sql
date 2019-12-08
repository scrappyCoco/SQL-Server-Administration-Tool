DECLARE @auditGuid UNIQUEIDENTIFIER = '%s';

DECLARE @logFilePath NVARCHAR(1000);

SELECT
        @logFilePath = log_file_path + N'*_' + CAST(@auditGuid AS NVARCHAR(1000)) + N'_*'
FROM sys.server_file_audits

SELECT TOP (1000) *
FROM master.sys.fn_get_audit_file(@logFilePath, NULL, NULL)
ORDER BY event_time DESC, sequence_number