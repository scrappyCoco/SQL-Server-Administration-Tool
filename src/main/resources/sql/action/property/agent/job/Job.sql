SELECT id                = CAST(sysjobs.job_id AS VARCHAR(50)),
       name              = sysjobs.name,
       isEnabled         = CAST(sysjobs.enabled AS BIT),
       description       = sysjobs.description,
       startStepId       = CAST(sysjobs.start_step_id AS VARCHAR(10)),
       categoryId        = CAST(syscategories.category_id AS VARCHAR(10)),
       categoryName      = syscategories.name,
       ownerName         = syslogins.name,
       dateCreated       = CAST(sysjobs.date_created AS VARCHAR(50)),
       lastModified      = CAST(sysjobs.date_modified AS VARCHAR(50)),
       lastExecuted      = (
                               SELECT TOP 1 CAST(sysjobhistory.run_date AS VARCHAR(20))
                               FROM msdb.dbo.sysjobhistory
                               WHERE sysjobhistory.job_id = sysjobs.job_id
                           ),
       eMailNotifyLevel  = CAST(NULLIF(sysjobs.notify_level_email, 0) AS TINYINT),
       eventLogLevel     = CAST(NULLIF(sysjobs.notify_level_eventlog, 0) AS TINYINT),
       eMailOperatorId   = CAST(NULLIF(sysjobs.notify_email_operator_id, 0) AS VARCHAR(10)),
       eMailOperatorName = sysoperators.name,
       deleteLevel       = CAST(NULLIF(sysjobs.delete_level, 0) AS TINYINT)
FROM msdb.dbo.sysjobs
LEFT JOIN sys.syslogins ON syslogins.sid = sysjobs.owner_sid
LEFT JOIN msdb.dbo.syscategories ON syscategories.category_id = sysjobs.category_id
LEFT JOIN msdb.dbo.sysoperators ON sysoperators.id = sysjobs.notify_email_operator_id;