CREATE TABLE #archive
(
    ArchiveNo  INT,
    CreateDate NVARCHAR(24),
    Size       INT
)
INSERT #archive
    EXEC master.sys.sp_enumerrorlogs 2


SELECT id   = CAST(ArchiveNo AS NVARCHAR(50)),
       name = 'Archive #' + CAST(Archive.ArchiveNo AS VARCHAR(100)) + ' ' + Archive.CreateDate,
       kind = 'AGENT_ERROR_LOG'
FROM #archive AS Archive
ORDER BY Name ASC

DROP TABLE #archive;