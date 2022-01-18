package com.stephenmatta;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;

@Introspected
public class TriggerRequestHandler extends MicronautRequestHandler<Input, String> {

    @Override
    public String execute(Input input) {
        return "Hello World!!";
    }
}
