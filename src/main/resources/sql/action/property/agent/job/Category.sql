SELECT id   = CAST(syscategories.category_id AS VARCHAR(10)),
       name = syscategories.name
from msdb.dbo.syscategories
where category_type = 1