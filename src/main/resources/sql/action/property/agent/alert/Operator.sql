-- DECLARE @alertId INT = ??alertId??;

SELECT id         = CAST(sysoperators.id AS VARCHAR(10)),
       name       = sysoperators.name,
       isSelected = CAST(IIF(sysnotifications.alert_id IS NULL, 0, 1) AS BIT),
       alertId    = CAST(sysnotifications.alert_id AS VARCHAR(100))
FROM msdb.dbo.sysoperators
LEFT JOIN msdb.dbo.sysnotifications ON sysnotifications.operator_id = sysoperators.id
        AND sysnotifications.notification_method = 1 -- e-mail
--        AND EXISTS(SELECT sysnotifications.alert_id INTERSECT SELECT @alertId);