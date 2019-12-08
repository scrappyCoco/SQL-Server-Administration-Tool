SELECT id        = CAST(sysjobs.job_id AS NVARCHAR(50)),
       name      = sysjobs.name,
       kind      = 'JOB',
       isEnabled = CAST(sysjobs.enabled AS BIT),
       isRunning = CAST(IIF(sysjobactivity.start_execution_date IS NOT NULL AND
                            sysjobactivity.stop_execution_date IS NULL
                                OR MaxActivity.SessionId IS NULL,
                            1,
                            0) AS BIT),
       isNotUsed = CAST(IIF(Schedule.HasSchedule IS NULL AND Alerts.HasAlert IS NULL, 1, 0) AS BIT),
       groupName = syscategories.name
FROM msdb.dbo.sysjobs
LEFT JOIN (
        SELECT SessionId = MAX(SubActivity.session_id),
               JobId     = SubActivity.job_id
        FROM msdb.dbo.sysjobactivity AS SubActivity
        GROUP BY SubActivity.job_id
          ) AS MaxActivity ON sysjobs.job_id = MaxActivity.JobId
LEFT JOIN msdb.dbo.sysjobactivity ON sysjobs.job_id = sysjobactivity.job_id
        AND MaxActivity.SessionId = sysjobactivity.session_id
LEFT JOIN msdb.dbo.syscategories ON syscategories.category_id = sysjobs.category_id
OUTER APPLY (
        SELECT TOP (1) HasAlert = 1
        FROM msdb.dbo.sysalerts
        WHERE sysalerts.job_id = sysjobs.job_id
            ) AS Alerts
OUTER APPLY (
        SELECT TOP (1) HasSchedule = 1
        FROM msdb.dbo.sysjobschedules
        WHERE sysjobschedules.job_id = sysjobs.job_id
            ) AS Schedule
ORDER BY sysjobs.name