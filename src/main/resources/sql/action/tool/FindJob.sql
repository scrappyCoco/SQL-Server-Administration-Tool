DECLARE @dbName  SYSNAME       = '??dbName??',
        @command NVARCHAR(MAX) = '??command??';

SELECT DISTINCT categoryName = syscategories.name,
                jobId        = sysjobs.job_id,
                jobName      = sysjobs.name
FROM msdb.dbo.sysjobsteps
INNER JOIN msdb.dbo.sysjobs ON sysjobs.job_id = sysjobsteps.job_id
INNER JOIN msdb.dbo.syscategories ON syscategories.category_id = sysjobs.category_id
WHERE sysjobsteps.database_name LIKE @dbName
  AND sysjobsteps.command LIKE @command
ORDER BY syscategories.name,
         sysjobs.name;