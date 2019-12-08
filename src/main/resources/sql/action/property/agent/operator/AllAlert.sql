SELECT id         = CAST(sysalerts.id AS VARCHAR(10)),
       name       = sysalerts.name,
       sendToMail = CAST(0 AS BIT)
FROM msdb.dbo.sysalerts;