-- MssqlCertificate: SimpleCertificate
CREATE CERTIFICATE [SimpleCertificate] AUTHORIZATION [MyUser]
  FROM ASSEMBLY [MyAssembly]
  ACTIVE FOR BEGIN_DIALOG = ON;
GO