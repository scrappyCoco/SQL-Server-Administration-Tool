DECLARE @scheduleId INT = ??scheduleId??;

SELECT id         = CAST(sysjobs.job_id AS VARCHAR(50)),
       name       = sysjobs.name,
       isSelected = CAST(IIF(sysjobschedules.job_id IS NOT NULL, 1, 0) AS BIT),
       kind       = 'JOB'
FROM msdb.dbo.sysjobs
LEFT JOIN msdb.dbo.sysjobschedules ON sysjobs.job_id = sysjobschedules.job_id
        AND sysjobschedules.schedule_id = @scheduleId;