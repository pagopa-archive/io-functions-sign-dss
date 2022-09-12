package it.pagopa.io.sign;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import it.pagopa.io.sign.model.Document;
import it.pagopa.io.sign.service.ProcessDocument;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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
  ) throws Exception {
    Logger logger = context.getLogger();

    try {
      ProcessDocument processor = new ProcessDocument(logger);
      Type documentsListType = new TypeToken<ArrayList<Document>>() {}.getType();

      List<Document> documentsList = gson.fromJson(message, documentsListType);

      for (Document document : documentsList) {
        if (document == null || document.getName() == null) {
          throw new JsonSyntaxException("json parameters is null");
        }
        processor.preProcess(document);
      }
    } catch (JsonSyntaxException e) {
      logger.severe(String.format("Invalid json on queue: %s", message));
      throw e;
    } catch (Exception e) {
      String errorMessage = String.format(
        "I can't process the message: %s\nError: %s",
        message
      );
      logger.severe(errorMessage);
      throw e;
    }
  }
}
