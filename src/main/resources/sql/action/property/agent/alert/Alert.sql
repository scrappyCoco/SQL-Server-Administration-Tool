DECLARE @tmp_sp_help_alert TABLE
                           (
                               id                        INT              NULL,
                               name                      NVARCHAR(128)    NULL,
                               event_source              NVARCHAR(100)    NULL,
                               event_category_id         INT              NULL,
                               event_id                  INT              NULL,
                               message_id                INT              NULL,
                               severity                  INT              NULL,
                               enabled                   TINYINT          NULL,
                               delay_between_responses   INT              NULL,
                               last_occurrence_date      INT              NULL,
                               last_occurrence_time      INT              NULL,
                               last_response_date        INT              NULL,
                               last_response_time        INT              NULL,
                               notification_message      NVARCHAR(512)    NULL,
                               include_event_description TINYINT          NULL,
                               database_name             NVARCHAR(128)    NULL,
                               event_description_keyword NVARCHAR(100)    NULL,
                               occurrence_count          INT              NULL,
                               count_reset_date          INT              NULL,
                               count_reset_time          INT              NULL,
                               job_id                    UNIQUEIDENTIFIER NULL,
                               job_name                  NVARCHAR(128)    NULL,
                               has_notification          INT              NULL,
                               flags                     INT              NULL,
                               performance_condition     NVARCHAR(512)    NULL,
                               category_name             NVARCHAR(128)    NULL,
                               wmi_namespace             NVARCHAR(MAX)    NULL,
                               wmi_query                 NVARCHAR(MAX)    NULL,
                               type                      INT              NULL
                           )
INSERT INTO @tmp_sp_help_alert EXEC msdb.dbo.sp_help_alert


SELECT id                   = CAST(id AS VARCHAR(10)),
       name                 = name,
       type                 = CAST(type AS VARCHAR(10)),
       isEnabled            = CAST(enabled AS BIT),
       -- region Event alert definition
       databaseName         = database_name,
       errorNumber          = CAST(message_id AS VARCHAR(10)),
       severity             = CAST(severity AS VARCHAR(10)),
       messageText          = event_description_keyword,
       -- endregion
       performanceCondition = performance_condition,
       wmiNamespace         = wmi_namespace,
       wmiQuery             = wmi_query,
       jobId                = CAST(job_id AS VARCHAR(50)),
       jobName              = job_name,
       -- region Options
       includeEmail         = CAST(IIF(has_notification & 1 = 1, 1, 0) AS BIT),
       notificationMessage  = notification_message,
       minutes              = delay_between_responses / 60,
       seconds              = delay_between_responses % 60
       -- endregion
FROM @tmp_sp_help_alert;