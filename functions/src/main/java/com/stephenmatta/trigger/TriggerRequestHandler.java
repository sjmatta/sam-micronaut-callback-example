package com.stephenmatta.trigger;

import com.stephenmatta.Input;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;

@Introspected
public class TriggerRequestHandler extends MicronautRequestHandler<Input, Void> {

    @Override
    public Void execute(Input input) {
        SfnClient.builder()
            .httpClient(ApacheHttpClient.create())
            .build()
            .startExecution(
                StartExecutionRequest.builder()
                    .stateMachineArn(System.getenv("WORKFLOW_ARN"))
                    .build()
            );
        return null;
    }
}
