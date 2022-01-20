package com.stephenmatta.sharedconfig;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sns.SnsClient;

@Factory
class AwsConfig {

    @Singleton
    SfnClient sfnClient() {
        return SfnClient.builder().httpClient(httpClient()).build();
    }

    @Singleton
    DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder().httpClient(httpClient()).build();
    }

    @Singleton
    LambdaClient lambdaClient() {
        return LambdaClient.builder().httpClient(httpClient()).build();
    }

    @Singleton
    SnsClient snsClient() {
        return SnsClient.builder().httpClient(httpClient()).build();
    }

    @Singleton
    SdkHttpClient httpClient() {
        return ApacheHttpClient.create();
    }
}
