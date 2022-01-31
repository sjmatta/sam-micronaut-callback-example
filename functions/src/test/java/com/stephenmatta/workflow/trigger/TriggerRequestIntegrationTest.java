package com.stephenmatta.workflow.trigger;

import io.micronaut.context.annotation.Value;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest(propertySources = "test")
class TriggerRequestIntegrationTest {

    @Inject
    private TriggerRequestHandler triggerRequestHandler;

    @Value("${workflow.arn}")
    private String workflowArn;

    @Captor
    private ArgumentCaptor<StartExecutionRequest> argumentCaptor;

    private final SfnClient mockSfnClient = mock(SfnClient.class);

    private AutoCloseable closeable;

    @BeforeEach
    public void open() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void release() throws Exception {
        closeable.close();
    }

    @MockBean(SfnClient.class)
    public SfnClient mockSfnClient() {
        return mockSfnClient;
    }

    @Test
    void testTriggerRequestHandler() {
        when(mockSfnClient.startExecution(any(StartExecutionRequest.class))).thenReturn(null);
        triggerRequestHandler.execute(new TriggerRequestInput());
        verify(mockSfnClient).startExecution(argumentCaptor.capture());

        assertEquals(argumentCaptor.getValue().getValueForField("stateMachineArn", String.class).get(), workflowArn);
    }
}
