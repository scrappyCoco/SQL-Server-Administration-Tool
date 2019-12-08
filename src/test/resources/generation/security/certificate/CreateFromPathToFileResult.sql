-- MssqlCertificate: MyCertificate
CREATE CERTIFICATE [MyCertificate]
  FROM EXECUTABLE FILE = 'C:\MyAssembly.dll' FILE = 'C:\MyPrivateKey.cer'
, ENCRYPTION BY PASSWORD = '>My $tronG pas$w0rd'
, DECRYPTION BY PASSWORD = '>My $tronG pas$w0rd';
GO