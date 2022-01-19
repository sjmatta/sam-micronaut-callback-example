package com.stephenmatta;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

@Introspected
public class InvokeAsyncApiHandler extends MicronautRequestHandler<Input, Void> {

    Logger log = LoggerFactory.getLogger(InvokeAsyncApiHandler.class);

    @Override
    public Void execute(Input input) {
        log.debug("{}", input);
        String correlationId = UUID.randomUUID().toString();
        DynamoDbClient.builder().httpClient(ApacheHttpClient.create()).build()
            .putItem(PutItemRequest.builder().tableName(System.getenv("TABLE_NAME")).item(
                Map.of("correlationId", AttributeValue.builder().s(correlationId).build(),
                    "taskToken", AttributeValue.builder().s(input.getTaskToken()).build(),
                    "expirationTime", AttributeValue.builder().n(String.valueOf(Instant.now().getEpochSecond() + 3_600)).build()
                )
            ).build());
        LambdaClient.builder().httpClient(ApacheHttpClient.create()).build()
            .invoke(InvokeRequest.builder().invocationType(InvocationType.EVENT)
                .functionName(System.getenv("LAMBDA_FUNCTION"))
                .payload(SdkBytes.fromString("{\"correlationId\": \"" + correlationId + "\"}", Charset.defaultCharset()))
                .build());
        return null;
    }
}
