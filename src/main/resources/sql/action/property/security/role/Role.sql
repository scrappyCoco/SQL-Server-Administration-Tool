DECLARE @roleId INT = ??roleId??;

SELECT id   = CAST(server_principals.principal_id AS VARCHAR(10)),
       name = server_principals.name
FROM master.sys.server_principals
WHERE principal_id = @roleId;