-- MssqlLogin: SimpleSqlLogin
CREATE LOGIN [SimpleSqlLogin] WITH PASSWORD = '_Pas$Word_' MUST_CHANGE,
  DEFAULT_DATABASE = [msdb],
  DEFAULT_LANGUAGE = us_english,
  CHECK_POLICY = ON,
  CHECK_EXPIRATION = ON;

DENY CONNECT SQL TO [SimpleSqlLogin];
REVOKE CONNECT SQL TO [SimpleSqlLogin];
GO