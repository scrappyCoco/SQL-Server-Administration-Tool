DECLARE
    @JobId UNIQUEIDENTIFIER = '%s';

EXEC msdb.dbo.sp_update_job @job_id = @JobId, @enabled = 1