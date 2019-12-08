DECLARE @JobId UNIQUEIDENTIFIER = '%s';

EXEC msdb.dbo.sp_stop_job @job_id = @JobId;