DECLARE @operatorId INT = ??operatorId??;

SELECT id         = CAST(sysalerts.id AS VARCHAR(10)),
       name       = sysalerts.name,
       sendToMail = CAST(IIF(sysnotifications.alert_id IS NOT NULL, 1, 0) AS BIT)
FROM msdb.dbo.sysalerts
LEFT JOIN msdb.dbo.sysnotifications ON sysalerts.id = sysnotifications.alert_id
        AND sysnotifications.operator_id = @operatorId;
