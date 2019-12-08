CREATE TABLE #Users
(
  db     SYSNAME,
  id     SYSNAME,
  name   SYSNAME,
  [user] SYSNAME
);

DECLARE
  @searchQuery NVARCHAR(1000);

SET @searchQuery = N'
USE [?];

INSERT INTO #Users (id, name, db, [user])
SELECT
  id     = server_principals.principal_id,
  name   = server_principals.name,
  db     = ''?'',
  [user] = sysusers.name
FROM sys.sysusers
     INNER JOIN sys.server_principals ON server_principals.sid = sysusers.sid;
';

EXEC sys.sp_MSforeachdb @searchQuery;

SELECT *
FROM #Users;

DROP TABLE #Users;