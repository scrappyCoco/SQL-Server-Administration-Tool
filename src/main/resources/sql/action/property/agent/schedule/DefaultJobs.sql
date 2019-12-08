SELECT id         = CAST(sysjobs.job_id AS VARCHAR(50)),
       name       = sysjobs.name,
       isSelected = CAST(0 AS BIT),
       kind       = 'JOB'
FROM msdb.dbo.sysjobs;