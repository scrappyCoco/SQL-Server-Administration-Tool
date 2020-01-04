DECLARE @operatorId INT = NULL;

SELECT id              = CAST(sysjobs.job_id AS VARCHAR(50)),
       name            = sysjobs.name,
       mailNotifyLevel = IIF(notify_email_operator_id = @operatorId, sysjobs.notify_level_email, NULL),
       isSelected      = CAST(IIF(notify_email_operator_id = @operatorId, 1, 0) AS BIT)
FROM msdb.dbo.sysjobs;