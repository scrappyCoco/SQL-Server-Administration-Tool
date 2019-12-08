SELECT id   = CAST(syslanguages.lcid AS VARCHAR(10)),
       name = syslanguages.name
FROM sys.syslanguages;