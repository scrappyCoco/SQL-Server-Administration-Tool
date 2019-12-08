SELECT id        = CAST(sysoperators.id AS VARCHAR(10)),
       name      = sysoperators.name,
       kind      = 'OPERATOR',
       groupName = syscategories.name,
       isEnabled = CAST(sysoperators.enabled AS BIT)
FROM msdb.dbo.sysoperators
LEFT JOIN msdb.dbo.syscategories ON syscategories.category_id = sysoperators.category_id
ORDER BY sysoperators.name;