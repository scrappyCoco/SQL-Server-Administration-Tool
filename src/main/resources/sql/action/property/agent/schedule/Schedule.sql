DECLARE @scheduleId INT = ??scheduleId??;

SELECT id                   = CAST(sysschedules.schedule_id AS VARCHAR(10)),
       name                 = sysschedules.name,
       enabled              = CAST(sysschedules.enabled AS BIT),
       freqType             = sysschedules.freq_type,
       freqInterval         = sysschedules.freq_interval,
       freqSubDayType       = sysschedules.freq_subday_type,
       freqSubDayInterval   = sysschedules.freq_subday_interval,
       freqRelativeInterval = sysschedules.freq_relative_interval,
       freqRecurrenceFactor = sysschedules.freq_recurrence_factor,
       activeStartDate      = sysschedules.active_start_date,
       activeEndDate        = sysschedules.active_end_date,
       activeStartTime      = sysschedules.active_start_time,
       activeEndTime        = sysschedules.active_end_time,
       ownerLoginName       = server_principals.name
FROM msdb.dbo.sysschedules
LEFT JOIN master.sys.server_principals ON server_principals.sid = sysschedules.owner_sid
WHERE sysschedules.schedule_id = @scheduleId;