package com.stephenmatta.workflow.apiclient;

class InvokeAsyncApiInput {

    private String taskToken;

    public String getTaskToken() {
        return taskToken;
    }

    public void setTaskToken(String taskToken) {
        this.taskToken = taskToken;
    }

    @Override
    public String toString() {
        return "InvokeAsyncApiInput{" +
            "taskToken='" + taskToken + '\'' +
            '}';
    }
}
