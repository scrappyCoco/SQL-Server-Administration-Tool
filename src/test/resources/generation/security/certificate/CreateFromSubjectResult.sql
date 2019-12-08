-- MssqlCertificate: MyCertificate
CREATE CERTIFICATE [MyCertificate]
  ENCRYPTION BY PASSWORD = 'My $tronG pas$w0rd'
  WITH SUBJECT = 'My subject'
  , START_DATE = '20190830';
GO

