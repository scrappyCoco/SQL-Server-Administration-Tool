SELECT id   = CAST(databases.database_id AS VARCHAR(10)),
       name = databases.name
FROM sys.databases
WHERE databases.state_desc = 'ONLINE';