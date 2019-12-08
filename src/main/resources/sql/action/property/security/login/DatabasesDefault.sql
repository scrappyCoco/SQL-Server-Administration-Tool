SELECT db            = databases.name,
       defaultSchema = NULL,
       id            = CAST(databases.database_id AS VARCHAR(10)),
       name          = name,
       [user]        = NULL,
       isSelected    = CAST(0 AS BIT)
FROM sys.databases
WHERE databases.state_desc = 'ONLINE'
ORDER BY databases.name;