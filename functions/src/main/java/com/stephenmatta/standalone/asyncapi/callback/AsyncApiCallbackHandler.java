package com.stephenmatta.standalone.asyncapi.callback;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.SendTaskSuccessRequest;

import java.util.Map;

@Introspected
public class AsyncApiCallbackHandler extends MicronautRequestHandler<SQSEvent, Void> {

    private static final Logger log = LoggerFactory.getLogger(AsyncApiCallbackHandler.class);

    @Inject
    private DynamoDbClient dynamoDbClient;

    @Inject
    private SfnClient sfnClient;

    @Inject
    private ObjectMapper objectMapper;

    @Override
    public Void execute(SQSEvent input) {
        input.getRecords().forEach(this::handleRecord);
        return null;
    }

    private void handleRecord(SQSEvent.SQSMessage sqsMessage) {
        try {
            doHandleRecord(sqsMessage);
        } catch (JsonProcessingException e) {
            log.error("Encountered an error during JSON processing", e);
            throw new RuntimeException(e);
        }
    }

    private void doHandleRecord(SQSEvent.SQSMessage sqsMessage) throws JsonProcessingException {
        JsonNode body = objectMapper.readTree(sqsMessage.getBody());
        JsonNode message = objectMapper.readTree(body.get("Message").asText());
        String correlationId = message.get("correlationId").asText();

        GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
            .tableName(System.getenv("TABLE_NAME"))
            .key(Map.of("correlationId", AttributeValue.builder().s(correlationId).build()))
            .build());

        String taskToken = response.item().get("taskToken").s();

        sfnClient.sendTaskSuccess(SendTaskSuccessRequest.builder()
            .taskToken(taskToken)
            .output("{}")
            .build());
    }
}
