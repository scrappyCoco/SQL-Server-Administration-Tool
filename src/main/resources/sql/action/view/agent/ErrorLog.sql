DECLARE
    @archiveNumber INT        = %s,
    @archiveSource INT        = 2, -- SQL Server Agent
    @filter1       NVARCHAR(200),
    @filter2       NVARCHAR(200),
    @dateFrom      DATE,
    @dateTo        DATE,
    @sortType      VARCHAR(5) = 'DESC',
    @instanceName  VARCHAR(200);

EXEC master.sys.xp_readerrorlog @archiveNumber,
     @archiveSource,
     @filter1,
     @filter2,
     @dateFrom,
     @dateTo,
     @sortType,
     @instanceName;