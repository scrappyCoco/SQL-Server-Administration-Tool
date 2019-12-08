SELECT id        = CAST(sysalerts.id AS NVARCHAR(50)),
       name      = sysalerts.name,
       kind      = 'ALERT',
       isEnabled = CAST(sysalerts.enabled AS BIT),
       groupName = syscategories.name
FROM msdb.dbo.sysalerts
LEFT JOIN msdb.dbo.syscategories ON sysalerts.category_id = syscategories.category_id
ORDER BY sysalerts.name;