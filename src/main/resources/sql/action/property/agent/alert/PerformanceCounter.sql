SELECT objectName   = object_name,
       counterName  = counter_name,
       instanceName = instance_name
FROM msdb.dbo.sysalerts_performance_counters_view
WHERE (cntr_type != 1073939712)
ORDER BY object_name,
         counter_name,
         instance_name;