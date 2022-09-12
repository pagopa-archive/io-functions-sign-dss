package it.pagopa.io.sign.service;

import com.azure.storage.blob.models.BlobProperties;
import it.pagopa.dss.SignatureService;
import it.pagopa.dss.SignatureServiceInterface;
import it.pagopa.dss.exception.SignatureServiceException;
import it.pagopa.io.sign.Configuration;
import it.pagopa.io.sign.azure.blobStorage.BlobStorageClient;
import it.pagopa.io.sign.azure.blobStorage.BlobStorageConfig;
import it.pagopa.io.sign.model.Document;
import it.pagopa.io.sign.utility.FileUtility;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
import javax.naming.ConfigurationException;

public final class ProcessDocument {

  BlobStorageConfig storageConfig;
  Logger logger;
  SignatureServiceInterface signatureServiceInterface;
  BlobStorageClient issuerContainerClient;
  BlobStorageClient issuerValidatedContainerClient;
  Configuration configuration;

  public ProcessDocument(Logger logger) throws ConfigurationException {
    this.storageConfig = BlobStorageConfig.getInstance();
    this.logger = logger;
    this.signatureServiceInterface = SignatureService.getInterface();
    this.configuration = Configuration.getInstance();

    this.issuerContainerClient =
      new BlobStorageClient(
        storageConfig.connectionString,
        storageConfig.issuerBlobContainerName
      );
    this.issuerValidatedContainerClient =
      new BlobStorageClient(
        storageConfig.connectionString,
        storageConfig.issuerValidatedBlobContainerName
      );
  }

  public void preProcess(Document document)
    throws IOException, SignatureServiceException, NoSuchAlgorithmException {
    String fileName = document.getName();
    Path tempFile = FileUtility.generateTempPath(fileName);

    BlobProperties issuerBlobProperties =
      this.issuerContainerClient.downloadBlob(fileName, tempFile);
    logger.info("downloaded File: " + tempFile.toString());

    Path tempPadesFile = FileUtility.generateTempPath(fileName + "_pades");
    FileOutputStream padesOutputStream = new FileOutputStream(tempPadesFile.toFile());
    this.signatureServiceInterface.generatePadesFile(
        tempFile.toFile(),
        padesOutputStream,
        this.configuration.signatureFieldId,
        this.configuration.signatureFieldTxt
      );
    padesOutputStream.close();
    logger.info("PAdES File generated: " + tempPadesFile.toString());

    String blobUrl = issuerValidatedContainerClient.uploadBlob(
      fileName,
      tempPadesFile,
      true,
      issuerBlobProperties
    );

    logger.info("PAdES File written on blob: " + blobUrl);
  }
}
