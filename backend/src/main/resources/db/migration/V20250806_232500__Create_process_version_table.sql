CREATE TABLE spms_process_version (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP,
    updated_by BIGINT,
    name VARCHAR(255) NOT NULL,
    `key` VARCHAR(255) NOT NULL,
    version VARCHAR(255) NOT NULL,
    bpmn_xml CLOB NOT NULL,
    status VARCHAR(50) NOT NULL,
    deployed_to_flowable BOOLEAN NOT NULL DEFAULT FALSE,
    flowable_definition_id VARCHAR(255),
    flowable_deployment_id VARCHAR(255),
    process_definition_id BIGINT,
    CONSTRAINT uk_process_version_key_version UNIQUE (`key`, version),
    CONSTRAINT fk_process_version_definition FOREIGN KEY (process_definition_id) REFERENCES spms_process_definition(id)
);
