SELECT id         = CAST(sysschedules.schedule_id AS VARCHAR(10)),
       name       = sysschedules.name,
       isSelected = CAST(0 AS BIT), -- CAST(IIF(sysjobschedules.schedule_id IS NOT NULL, 1, 0) AS BIT),
       isEnabled  = CAST(0 AS BIT),--CAST(sysschedules.enabled AS BIT)
       jobId      = CAST(sysjobschedules.job_id AS VARCHAR(100))
FROM msdb.dbo.sysschedules
LEFT JOIN msdb.dbo.sysjobschedules ON sysjobschedules.schedule_id = sysschedules.schedule_id
--        AND sysjobschedules.job_id = @jobId
ORDER BY sysschedules.name;