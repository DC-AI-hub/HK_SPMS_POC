package com.spms.backend.controller.process;

import com.spms.backend.service.model.process.ProcessVersionModel;
import com.spms.backend.service.model.process.VersionStatus;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public class TestUtils {
    public static ProcessVersionModel createTestVersion(String version, VersionStatus status) throws Exception {
        ProcessVersionModel model = new ProcessVersionModel();
        model.setFlowableDefinitionId(UUID.randomUUID().toString());
        model.setVersion(version);
        model.setStatus(status);
        model.setCreatedById(1L);
        model.setCreatedAt(new Date().getTime());
        model.setUpdatedById(1L);
        model.setName("hr-" + version);
        model.setKey("hr-" + version);

        model.setBpmnXml(new String(
                TestUtils.class.getClassLoader()
                        .getResourceAsStream("processes/it/"+version + ".xml")
                        .readAllBytes(), StandardCharsets.UTF_8));

        model.setDeployedToFlowable(false);
        return model;
    }
}
