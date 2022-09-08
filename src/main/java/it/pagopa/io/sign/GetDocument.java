package it.pagopa.io.sign;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import it.pagopa.io.sign.azure.blobStorage.BlobStorageClient;
import it.pagopa.io.sign.azure.blobStorage.BlobStorageConfig;
import it.pagopa.io.sign.model.Document;
import it.pagopa.io.sign.model.FileTBS;
import it.pagopa.io.sign.utility.FileUtility;
import java.nio.file.Path;
import java.util.logging.Logger;

public class GetDocument {

  Gson gson = new Gson();

  @FunctionName("GetDocument")
  public void run(
    @QueueTrigger(
      name = "GetDocument",
      queueName = "%WaitingDocumentQueueName%",
      connection = "AzureWebJobsStorage"
    ) String message,
    final ExecutionContext context
  ) {
    Logger logger = context.getLogger();

    try {
      BlobStorageConfig storageConfig = new BlobStorageConfig();
      Document document = gson.fromJson(message, Document.class);

      if (document == null || document.getName() == null) {
        throw new JsonSyntaxException("json parameters is null");
      }

      String fileName = document.getName();

      BlobContainerClient containerClient = BlobStorageClient.createContainerClient(
        storageConfig.connectionString,
        storageConfig.issuerBlobContainerName
      );
      BlobClient selectedBlob = BlobStorageClient.selectBlob(containerClient, fileName);

      if (BlobStorageClient.blobExist(selectedBlob)) {
        logger.info("File exist: " + selectedBlob.getBlobUrl());

        Path tempFile = FileUtility.generateTempPath(fileName);
        selectedBlob.downloadToFile(tempFile.toString());

        FileTBS fileTbs = new FileTBS(tempFile.toFile());

        logger.info("URL: " + tempFile.toString());
      } else {
        logger.warning("File not found: " + document.getName());
      }
    } catch (JsonSyntaxException e) {
      logger.warning("Invalid json on queue: " + message);
    } catch (Exception e) {
      logger.warning(e.getMessage());
    }
  }
}
