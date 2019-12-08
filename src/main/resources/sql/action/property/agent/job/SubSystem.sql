SELECT id   = CAST(subsystem_id AS VARCHAR(10)),
       name = subsystem
FROM msdb.dbo.syssubsystems
ORDER BY subsystem_id;