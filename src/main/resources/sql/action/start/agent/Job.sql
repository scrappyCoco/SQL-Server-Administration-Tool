DECLARE @JobId UNIQUEIDENTIFIER = '%s';

EXEC msdb.dbo.sp_start_job @job_id = @JobId;