-- DECLARE @jobId UNIQUEIDENTIFIER = ??jobId??;

SELECT id         = CAST(id AS VARCHAR(10)),
       name       = name,
       isSelected = CAST(0 AS BIT),-- CAST(IIF(job_id = @jobId, 1, 0) AS BIT),
       isEnabled  = CAST(0 AS BIT), /*CAST(CASE
                             WHEN job_id = @jobId THEN 1
                             WHEN job_id = '00000000-0000-0000-0000-000000000000' THEN 1
                             ELSE 0
           END AS BIT)*/
       jobId      = CAST(job_id AS VARCHAR(100))
FROM msdb.dbo.sysalerts
ORDER BY name;