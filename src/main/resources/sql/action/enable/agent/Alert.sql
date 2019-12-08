DECLARE @AlertName NVARCHAR(200) = N'%s';

EXEC msdb.dbo.sp_update_alert
     @name = @AlertName,
     @enabled = 0;