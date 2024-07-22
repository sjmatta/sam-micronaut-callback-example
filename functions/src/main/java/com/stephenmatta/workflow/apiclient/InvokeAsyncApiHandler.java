package com.stephenmatta.workflow.apiclient;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Introspected
public class InvokeAsyncApiHandler extends MicronautRequestHandler<InvokeAsyncApiInput, Void> {

    @Inject
    private DynamoDbClient dynamoDbClient;

    @Inject
    private LambdaClient lambdaClient;

    @Override
    public Void execute(InvokeAsyncApiInput input) {
        String tableName = System.getenv("TABLE_NAME");
        String correlationId = UUID.randomUUID().toString();
        saveTaskToken(tableName, correlationId, input.getTaskToken());

        String lambdaFunction = System.getenv("LAMBDA_FUNCTION");
        invokeLambdaFunction(lambdaFunction, correlationId);

        return null;
    }

    private void saveTaskToken(String tableName, String correlationId, String taskToken) {
        Map<String, AttributeValue> item = Map.of(
            "correlationId", toAttribute(correlationId),
            "taskToken", toAttribute(taskToken),
            "expirationTime", toAttribute(getExpirationTime()));
        dynamoDbClient.putItem(PutItemRequest.builder().tableName(tableName).item(item).build());
    }

    private void invokeLambdaFunction(String lambdaFunction, String correlationId) {
        lambdaClient.invoke(
            InvokeRequest.builder().invocationType(InvocationType.EVENT)
                .functionName(lambdaFunction)
                .payload(getCorrelationIdJsonBytes(correlationId))
                .build());
    }

    private Long getExpirationTime() {
        return Instant.now().getEpochSecond() + 86_400;
    }

    private SdkBytes getCorrelationIdJsonBytes(String correlationId) {
        return SdkBytes.fromString("{\"correlationId\": \"" + correlationId + "\"}", Charset.defaultCharset());
    }

    private AttributeValue toAttribute(String value) {
        return AttributeValue.builder().s(value).build();
    }

    private AttributeValue toAttribute(Long value) {
        return AttributeValue.builder().n(String.valueOf(value)).build();
    }
}
