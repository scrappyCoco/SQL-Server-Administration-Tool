DECLARE @jobId UNIQUEIDENTIFIER = ??jobId??;

SELECT id         = CAST(sysschedules.schedule_id AS VARCHAR(10)),
       name       = sysschedules.name,
       isSelected = CAST(IIF(sysjobschedules.schedule_id IS NOT NULL, 1, 0) AS BIT),
       isEnabled  = CAST(sysschedules.enabled AS BIT)
FROM msdb.dbo.sysschedules
LEFT JOIN msdb.dbo.sysjobschedules ON sysjobschedules.schedule_id = sysschedules.schedule_id
        AND sysjobschedules.job_id = @jobId
ORDER BY sysschedules.name;