package com.stephenmatta;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Introspected
public class AsyncApiHandler extends MicronautRequestHandler<Input, String> {

    private static final Logger log = LoggerFactory.getLogger(AsyncApiHandler.class);

    @Override
    public String execute(Input input) {
        PublishResponse response = SnsClient.builder().httpClient(ApacheHttpClient.create()).build().publish(
            PublishRequest.builder()
                .topicArn(System.getenv("TOPIC_ARN"))
                .message("{\"correlationId\": \"" + input.getCorrelationId() + "\"}")
                .build()
        );
        log.debug("{}", response);
        return response.toString();
    }
}
