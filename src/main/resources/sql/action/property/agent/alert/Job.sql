SELECT id   = CAST(sysjobs.job_id AS VARCHAR(50)),
       name = sysjobs.name
FROM msdb.dbo.sysjobs
ORDER BY sysjobs.name;