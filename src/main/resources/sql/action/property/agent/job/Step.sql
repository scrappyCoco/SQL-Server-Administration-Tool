DECLARE @jobId UNIQUEIDENTIFIER = '??jobId??';

SELECT id              = CAST(sysjobsteps.step_id AS VARCHAR(50)),
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
       outputFile      = sysjobsteps.output_file_name,
       appendFile      = CAST(IIF(sysjobsteps.flags & 2 = 2, 0, 1) AS BIT),
       overrideFile    = CAST(IIF(sysjobsteps.flags & 2 = 2, 1, 0) AS BIT),
       appendTable     = CAST(IIF(sysjobsteps.flags & 8 = 8, 0, 1) AS BIT),
       overrideTable   = CAST(IIF(sysjobsteps.flags & 16 = 16, 1, 0) AS BIT),
       stepHistory     = CAST(IIF(sysjobsteps.flags & 4 = 4, 1, 0) AS BIT),
       jobHistory      = CAST(IIF(sysjobsteps.flags & 32 = 32, 1, 0) AS BIT),
       abortEvent      = CAST(IIF(sysjobsteps.flags & 64 = 64, 1, 0) AS BIT)
FROM msdb.dbo.sysjobsteps
LEFT JOIN msdb.dbo.sysproxies ON sysproxies.proxy_id = sysjobsteps.proxy_id
WHERE sysjobsteps.job_id = @jobId
ORDER BY sysjobsteps.step_id;