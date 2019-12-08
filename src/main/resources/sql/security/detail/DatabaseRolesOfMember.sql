CREATE TABLE #Roles
(
  id      SYSNAME NOT NULL, -- Role id.
  name    SYSNAME NOT NULL, -- Role name.
  db      SYSNAME NOT NULL, -- Database name.
  loginId SYSNAME NOT NULL  -- User.
);

DECLARE
  @searchQuery NVARCHAR(1000);

SET @searchQuery = N'
USE [?];

DECLARE @databaseRoles TABLE (
  RoleName  SYSNAME,
  RoleId    SMALLINT,
  IsAppRole BIT
);

INSERT INTO @databaseRoles (RoleName, RoleId, IsAppRole)
  EXEC sp_helprole;

INSERT INTO #Roles (id, name, db, loginId)
SELECT
  id      = CAST(DatabaseRoles.RoleId AS SYSNAME),
  name    = DatabaseRoles.RoleName,
  db      = ''?'',
  loginId = CAST(server_principals.principal_id AS SYSNAME)
FROM @databaseRoles AS DatabaseRoles
     INNER JOIN sys.database_role_members ON database_role_members.role_principal_id = DatabaseRoles.RoleId
     INNER JOIN sys.sysusers ON sysusers.uid = database_role_members.member_principal_id
     INNER JOIN sys.server_principals ON server_principals.sid = sysusers.sid;
';

EXEC sys.sp_MSforeachdb @searchQuery;

SELECT *
FROM #Roles;

DROP TABLE #Roles;