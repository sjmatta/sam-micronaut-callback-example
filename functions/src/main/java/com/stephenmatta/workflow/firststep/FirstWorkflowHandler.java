package com.stephenmatta.workflow.firststep;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;

@Introspected
public class FirstWorkflowHandler extends MicronautRequestHandler<FirstWorkflowInput, String> {

    @Override
    public String execute(FirstWorkflowInput input) {
        return "sample data";
    }
}
