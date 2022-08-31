package it.pagopa.io.sign;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import java.util.Optional;

public class Info {

  @FunctionName("info")
  public HttpResponseMessage run(
    @HttpTrigger(
      name = "req",
      methods = { HttpMethod.GET },
      authLevel = AuthorizationLevel.ANONYMOUS
    ) HttpRequestMessage<Optional<String>> request,
    final ExecutionContext context
  ) {
    context.getLogger().info("info endpoint triggered!");
    return request
      .createResponseBuilder(HttpStatus.BAD_REQUEST)
      .body("It works!")
      .build();
  }
}
