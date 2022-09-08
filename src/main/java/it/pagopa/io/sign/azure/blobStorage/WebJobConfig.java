package it.pagopa.io.sign.azure.blobStorage;

import javax.naming.ConfigurationException;

public class WebJobConfig {

  public String connectionString;
  public String waitingDocumentQueueName;
  public String padesBlobContainerName;

  public WebJobConfig() throws ConfigurationException {
    connectionString = System.getenv("AzureWebJobsStorage");
    if (connectionString == null) {
      throw new ConfigurationException("AzureWebJobsStorage not set!");
    }
    waitingDocumentQueueName = System.getenv("WaitingDocumentQueueName");
    if (waitingDocumentQueueName == null) {
      waitingDocumentQueueName = "waiting-for-document";
    }
    padesBlobContainerName = System.getenv("PadesBlobContainerName");
    if (padesBlobContainerName == null) {
      padesBlobContainerName = "pades-documents";
    }
  }
}
