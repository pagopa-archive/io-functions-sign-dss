package it.pagopa.io.sign.azure.blobStorage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

public final class BlobStorageClient {

  private BlobStorageClient() {
    throw new IllegalStateException(
      "You cannot initialize this class (BlobStorageClient)"
    );
  }

  public static BlobContainerClient createContainerClient(
    String connectionString,
    String containerName
  ) {
    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
      .connectionString(connectionString)
      .buildClient();
    return blobServiceClient.getBlobContainerClient(containerName);
  }

  public static BlobClient selectBlob(
    BlobContainerClient containerClient,
    String blobName
  ) {
    return containerClient.getBlobClient(blobName);
  }

  public static boolean blobExist(BlobClient blobClient) {
    return blobClient.exists();
  }
}
