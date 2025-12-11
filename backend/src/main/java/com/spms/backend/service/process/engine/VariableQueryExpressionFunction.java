package com.spms.backend.service.process.engine;

import org.flowable.common.engine.api.delegate.FlowableFunctionDelegate;

import java.lang.reflect.Method;
import java.util.Collection;

public class VariableQueryExpressionFunction implements FlowableFunctionDelegate {
    @Override
    public String prefix() {
        return "";
    }

    @Override
    public Collection<String> prefixes() {
        return FlowableFunctionDelegate.super.prefixes();
    }

    @Override
    public String localName() {
        return "";
    }

    @Override
    public Collection<String> localNames() {
        return FlowableFunctionDelegate.super.localNames();
    }

    @Override
    public Method functionMethod() {
        return null;
    }
}
