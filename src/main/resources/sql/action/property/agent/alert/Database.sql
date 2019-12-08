SELECT id   = CAST(database_id AS VARCHAR(10)),
       name = name
FROM sys.databases
WHERE databases.state_desc = 'ONLINE';