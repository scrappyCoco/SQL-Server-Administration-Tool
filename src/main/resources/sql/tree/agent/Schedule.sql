SELECT id        = CAST(sysschedules.schedule_id AS NVARCHAR(50)),
       name      = sysschedules.name,
       kind      = 'SCHEDULE',
       isNotUsed = CAST(IIF(SUM(IIF(sysjobschedules.job_id IS NULL, 0, 1)) > 0, 0, 1) AS BIT),
       isEnabled = CAST(sysschedules.enabled AS BIT)
FROM msdb.dbo.sysschedules
LEFT JOIN msdb.dbo.sysjobschedules ON sysschedules.schedule_id = sysjobschedules.schedule_id
GROUP BY sysschedules.schedule_id,
         sysschedules.name,
         sysschedules.enabled
ORDER BY sysschedules.name;