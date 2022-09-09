package it.pagopa.io.sign.azure.blobStorage;

import com.azure.core.http.rest.Response;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.BlockBlobItem;
import com.azure.storage.blob.models.DownloadRetryOptions;
import com.azure.storage.blob.models.ParallelTransferOptions;
import com.azure.storage.blob.options.BlobDownloadToFileOptions;
import com.azure.storage.blob.options.BlobUploadFromFileOptions;
import it.pagopa.io.sign.exception.FileStorageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;

public class BlobStorageClient {

  static final Duration TIMEOUT = Duration.ofSeconds(10);
  static final int N_RETRY_DOWNLOAD = 3;
  static final Long BLOCK_SIZE = 100 * 1024 * 1024L; // 100 MB;

  private BlobServiceClient serviceClient;
  private BlobContainerClient containerClient;

  public BlobStorageClient(String connectionString, String containerName) {
    this.serviceClient =
      new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
    this.containerClient = this.serviceClient.getBlobContainerClient(containerName);
  }

  public BlobProperties downloadBlob(String blobName, Path filePath) {
    BlobClient blobClient = containerClient.getBlobClient(blobName);
    if (blobClient.exists()) {
      Response<BlobProperties> response = blobClient.downloadToFileWithResponse(
        new BlobDownloadToFileOptions(filePath.toString())
          .setDownloadRetryOptions(
            new DownloadRetryOptions().setMaxRetryRequests(N_RETRY_DOWNLOAD)
          )
          .setOpenOptions(
            new HashSet<>(
              Arrays.asList(
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE,
                StandardOpenOption.READ
              )
            )
          ),
        TIMEOUT,
        Context.NONE
      );

      if (is2xxSuccessful(response.getStatusCode())) {
        return response.getValue();
      } else {
        throw new FileStorageException(
          String.format(
            "Unable to download the file from storage. Response code: %d",
            response.getStatusCode()
          )
        );
      }
    } else {
      throw new FileStorageException(
        String.format("File %s not found on storage!", blobName)
      );
    }
  }

  public String uploadBlob(
    String blobName,
    Path filePath,
    boolean replaceIfExist,
    BlobProperties properties
  ) throws IOException {
    BlobClient blobClient = containerClient.getBlobClient(blobName);
    if (!replaceIfExist && blobClient.exists()) {
      throw new FileStorageException(
        String.format("File %s already exist on storage!", blobName)
      );
    }

    InputStream is = Files.newInputStream(filePath);
    byte[] digest = DigestUtils.md5(is);
    is.close();

    BlobHttpHeaders headers = new BlobHttpHeaders()
      .setContentMd5(digest)
      .setContentType(properties.getContentType())
      .setContentDisposition(properties.getContentDisposition());

    Map<String, String> metadata = properties.getMetadata();

    ParallelTransferOptions parallelTransferOptions = new ParallelTransferOptions()
      .setBlockSizeLong(BLOCK_SIZE);

    Response<BlockBlobItem> response = blobClient.uploadFromFileWithResponse(
      new BlobUploadFromFileOptions(filePath.toString())
        .setParallelTransferOptions(parallelTransferOptions)
        .setHeaders(headers)
        .setMetadata(metadata),
      TIMEOUT,
      Context.NONE
    );

    if (is2xxSuccessful(response.getStatusCode())) {
      return blobClient.getBlobUrl();
    } else {
      throw new FileStorageException("File not uploaded!");
    }
  }

  private boolean is2xxSuccessful(int responseCode) {
    return responseCode >= 200 && responseCode < 300;
  }
}
