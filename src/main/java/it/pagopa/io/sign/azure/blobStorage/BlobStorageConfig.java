package it.pagopa.io.sign.azure.blobStorage;

import javax.naming.ConfigurationException;

public final class BlobStorageConfig {

  public String connectionString;
  public String issuerBlobContainerName;
  public String issuerValidatedBlobContainerName;
  public int timeout;
  public int numberOfRetryDownload;
  public int blockSize;
  public WebJobConfig webJobConfig = WebJobConfig.getInstance();

  private BlobStorageConfig() throws ConfigurationException {
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
    try {
      timeout = Integer.parseInt(System.getenv("BlobStorageTimeoutInSecond"));
    } catch (NumberFormatException e) {
      timeout = 10;
    }
    try {
      numberOfRetryDownload = Integer.parseInt(System.getenv("NumberOfRetryDownload"));
    } catch (NumberFormatException e) {
      numberOfRetryDownload = 3;
    }
  }

  private static class BlobStorageConfigHelper {

    private static final BlobStorageConfig INSTANCE;

    static {
      try {
        INSTANCE = new BlobStorageConfig();
      } catch (Exception e) {
        throw new ExceptionInInitializerError(e);
      }
    }
  }

  public static BlobStorageConfig getInstance() {
    return BlobStorageConfigHelper.INSTANCE;
  }
}
