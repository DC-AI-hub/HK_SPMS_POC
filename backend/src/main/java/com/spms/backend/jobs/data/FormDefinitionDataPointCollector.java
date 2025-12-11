package com.spms.backend.jobs.data;

import com.spms.backend.service.process.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FormDefinitionDataPointCollector implements DataPointCollector {

    @Autowired
    private FormService formService;

    @Override
    public String name() {
        return "statistics:form.definition.count.name";
    }

    @Override
    public String description() {
        return "statistics:form.definition.count.description";
    }

    @Override
    public Long getValueCurrentValue() {
        return formService.countForms();
    }
}
