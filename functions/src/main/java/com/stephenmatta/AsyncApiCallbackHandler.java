package com.stephenmatta;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
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

    @Override
    public Void execute(SQSEvent input) {
        log.debug("{}", input);
        ObjectMapper mapper = new ObjectMapper();
        input.getRecords().forEach(record -> {
            try {
                JsonNode body = mapper.readTree(record.getBody());
                JsonNode message = mapper.readTree(body.get("Message").asText());
                String correlationId = message.get("correlationId").asText();

                log.debug("Correlation ID: {}", correlationId);

                GetItemResponse response = DynamoDbClient.builder().httpClient(ApacheHttpClient.create()).build()
                    .getItem(GetItemRequest.builder()
                        .tableName(System.getenv("TABLE_NAME"))
                        .key(Map.of("correlationId", AttributeValue.builder().s(correlationId).build()))
                        .build());
                log.debug("{}", response);

                String taskToken = response.item().get("taskToken").s();

                log.debug("Found task token: {}", taskToken);
                SfnClient.builder().httpClient(ApacheHttpClient.create()).build()
                    .sendTaskSuccess(SendTaskSuccessRequest.builder()
                        .taskToken(taskToken)
                        .output("{}")
                        .build());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        return null;
    }
}
