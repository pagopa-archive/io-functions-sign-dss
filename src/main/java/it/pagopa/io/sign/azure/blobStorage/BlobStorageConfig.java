package it.pagopa.io.sign.azure.blobStorage;

import javax.naming.ConfigurationException;

public class BlobStorageConfig {

  public String connectionString;
  public String issuerBlobContainerName;
  public String issuerValidatedBlobContainerName;
  public WebJobConfig webJobConfig = new WebJobConfig();

  public BlobStorageConfig() throws ConfigurationException {
    connectionString = System.getenv("StorageAccountConnectionString");
    if (connectionString == null) {
      throw new ConfigurationException("StorageAccountConnectionString not set!");
    }
    issuerBlobContainerName = System.getenv("IssuerBlobContainerName");
    if (issuerBlobContainerName == null) {
      issuerBlobContainerName = "documents";
    }
    issuerValidatedBlobContainerName = System.getenv("IssuerValidatedBlobContainerName");
    if (issuerValidatedBlobContainerName == null) {
      issuerValidatedBlobContainerName = "documents-validated";
    }
  }
}
