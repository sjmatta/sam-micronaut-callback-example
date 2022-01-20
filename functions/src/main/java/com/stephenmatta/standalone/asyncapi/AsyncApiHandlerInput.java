package com.stephenmatta.standalone.asyncapi;

class AsyncApiHandlerInput {

    private String correlationId;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    @Override
    public String toString() {
        return "AsyncApiHandlerInput{" +
            "correlationId='" + correlationId + '\'' +
            '}';
    }
}
