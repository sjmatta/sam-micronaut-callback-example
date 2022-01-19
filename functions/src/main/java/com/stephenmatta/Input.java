package com.stephenmatta;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Input {

    private String correlationId;
    private String taskToken;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getTaskToken() {
        return taskToken;
    }

    public void setTaskToken(String taskToken) {
        this.taskToken = taskToken;
    }

    @Override
    public String toString() {
        return "Input{" +
            "correlationId='" + correlationId + '\'' +
            ", taskToken='" + taskToken + '\'' +
            '}';
    }
}
