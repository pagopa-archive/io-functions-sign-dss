package it.pagopa.io.sign;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;

public class GetDocument {

  @FunctionName("GetDocument")
  public void run(
    @QueueTrigger(
      name = "GetDocument",
      queueName = "%WaitingDocumentQueueName%",
      connection = "AzureWebJobsStorage"
    ) String message,
    final ExecutionContext context
  ) {
    context.getLogger().info("Queue triggered!" + message);
  }
}
