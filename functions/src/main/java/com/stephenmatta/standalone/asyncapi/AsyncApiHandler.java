package com.stephenmatta.standalone.asyncapi;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Introspected
public class AsyncApiHandler extends MicronautRequestHandler<AsyncApiHandlerInput, String> {

    @Inject
    private SnsClient snsClient;

    @Override
    public String execute(AsyncApiHandlerInput input) {
        PublishResponse response = snsClient.publish(
            PublishRequest.builder()
                .topicArn(System.getenv("TOPIC_ARN"))
                .message(getCorrelationIdJson(input.getCorrelationId()))
                .build());
        return response.toString();
    }

    private String getCorrelationIdJson(String correlationId) {
        return "{\"correlationId\": \"" + correlationId + "\"}";
    }
}
