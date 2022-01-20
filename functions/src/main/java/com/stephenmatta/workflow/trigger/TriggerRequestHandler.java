package com.stephenmatta.workflow.trigger;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;

@Introspected
public class TriggerRequestHandler extends MicronautRequestHandler<TriggerRequestInput, Void> {

    @Inject
    private SfnClient sfnClient;

    @Override
    public Void execute(TriggerRequestInput input) {
        String stateMachineArn = System.getenv("WORKFLOW_ARN");
        StartExecutionRequest request = createRequest(stateMachineArn);
        sfnClient.startExecution(request);
        return null;
    }

    private StartExecutionRequest createRequest(String stateMachineArn) {
        return StartExecutionRequest.builder().stateMachineArn(stateMachineArn).build();
    }
}
