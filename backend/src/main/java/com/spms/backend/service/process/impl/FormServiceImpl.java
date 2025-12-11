package com.spms.backend.service.process.impl;

import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.repository.entities.process.FormVersionEntity;
import com.spms.backend.repository.process.FormVersionRepository;
import com.spms.backend.service.model.process.FormDefinitionModel;
import com.spms.backend.service.model.process.FormVersionModel;
import com.spms.backend.service.process.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FormServiceImpl implements FormService {
    private static final Logger logger = LoggerFactory.getLogger(FormServiceImpl.class);

    private final FormVersionRepository formVersionRepository;

    @Autowired
    public FormServiceImpl(FormVersionRepository formVersionRepository) {
        this.formVersionRepository = formVersionRepository;
    }

    /**
     * 创建表单版本。
     *
     * @param key   表单的唯一标识符。
     * @param model 表单定义模型，包含版本信息和表单定义内容。
     * @return 返回创建的 {@link FormVersionModel} 实例。
     * @throws IllegalArgumentException 如果指定版本的表单已存在。
     *  该方法会检查版本是否唯一，若版本已存在则抛出异常。否则，将表单定义保存为新的版本，
     *  并设置发布时间为当前时间戳，标记为非废弃状态。
     */
    @Override
    @Transactional
    public FormVersionModel createFormVersion(String key, FormDefinitionModel model) {
        logger.info("Creating form version for key: {}, version: {}", key, model.getVersion());
        Long versionLong = FormVersionModel.convertVersionStringToLong(model.getVersion());

        // Check version uniqueness
        if (formVersionRepository.findByKeyAndVersion(key, versionLong).isPresent()) {
            logger.warn("Version already exists for form: key={}, version={}", key, model.getVersion());
            throw new IllegalArgumentException("Version already exists for form: " + key);
        }

        FormVersionEntity entity = new FormVersionEntity();
        entity.setKey(key);
        entity.setVersion(versionLong);
        entity.setFormDefinition(model.getDefinition());
        entity.setPublishedDate(System.currentTimeMillis());
        entity.setDeprecated(false);
        entity.setDeploymentId(null);
        entity.setDescription(model.getDescription());
        entity.setName(model.getName());
        FormVersionEntity savedEntity = formVersionRepository.save(entity);
        logger.info("Form version created successfully for key: {}, version: {}", key, model.getVersion());
        return FormVersionModel.fromEntity(savedEntity);
    }

    /**
     * 根据表单键和版本号获取表单版本模型。
     *
     * @param key     表单的唯一标识键。
     * @param version 表单的版本号（字符串格式）。
     * @return 匹配的表单版本模型。
     * @throws NotFoundException 如果未找到指定版本的表单。
     */
    @Override
    @Transactional
    public FormVersionModel getVersion(String key, String version) {
        logger.debug("Fetching form version for key: {}, version: {}", key, version);
        Long versionLong = FormVersionModel.convertVersionStringToLong(version);
        return formVersionRepository.findByKeyAndVersion(key, versionLong)
            .map(FormVersionModel::fromEntity)
            .orElseThrow(() -> {
                logger.error("Form version not found for key: {}, version: {}", key, version);
                return new NotFoundException("Version not found: " + version);
            });
    }

    /**
     * 标记指定键和版本的表单版本为已弃用状态。
     * 如果版本不存在或已被弃用，则不执行任何操作。
     *
     * @param key     表单的唯一标识符，不能为 null。
     * @param version 表单的版本号，不能为 null。
     * @throws NotFoundException    如果指定的版本不存在。
     * @throws NullPointerException 如果 key 或 version 为 null。
     */
    @Override
    @Transactional
    public void deprecateVersion(String key, String version) {
        logger.info("Deprecating form version for key: {}, version: {}", key, version);
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(version, "Version cannot be null");

        Long versionLong = FormVersionModel.convertVersionStringToLong(version);
        FormVersionEntity entity = formVersionRepository.findByKeyAndVersion(key, versionLong)
            .orElseThrow(() -> {
                logger.error("Form version not found for deprecation: key={}, version={}", key, version);
                return new NotFoundException("Version not found: " + version);
            });

        if (!entity.isDeprecated()) {
            entity.setDeprecated(true);
            formVersionRepository.save(entity);
            logger.info("Form version deprecated successfully for key: {}, version: {}", key, version);
        } else {
            logger.warn("Form version already deprecated for key: {}, version: {}", key, version);
        }
    }

    /**
     * 获取指定表单键（key）的最新版本。
     *
     * @param key 表单的唯一标识符。
     * @return 返回最新版本的 {@link FormVersionModel} 对象。
     * @throws NotFoundException 如果未找到任何版本时抛出异常。
     */
    @Override
    @Transactional
    public FormVersionModel getLatestVersion(String key) {
        logger.debug("Fetching latest version for form key: {}", key);
        return formVersionRepository.findFirstByKeyOrderByVersionDesc(key)
            .map(FormVersionModel::fromEntity)
            .orElseThrow(() -> {
                logger.warn("No versions found for form key: {}", key);
                return new NotFoundException("No versions found for form: " + key);
            });
    }

    /**
     * 根据表单键（key）列出所有版本的表单数据，并按发布时间降序排序。
     *
     * @param key 表单的唯一标识键
     * @return 包含表单版本模型的列表，按发布时间降序排列
     */
    @Override
    @Transactional
    public List<FormVersionModel> listVersions(String key) {
        // Using existing repository method with un-paged
        return formVersionRepository.findByKeyOrderByPublishedDateDesc(key, Pageable.unpaged())
            .stream()
            .map(FormVersionModel::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 批量更新表单版本的废弃状态。
     *
     * @param keys       表单的唯一标识符列表，用于查找需要更新的表单版本。
     * @param deprecated 是否将表单版本标记为废弃（true 为废弃，false 为非废弃）。
     * @return 成功更新的表单版本数量。
     * @Transactional 确保方法在事务中执行，保证数据一致性。
     */
    @Override
    @Transactional
    public int batchUpdateDeprecatedStatus(List<String> keys, boolean deprecated) {
        int count = 0;
        for (String key : keys) {
            // Get all versions for this key
            List<FormVersionEntity> versions = formVersionRepository
                .findByKeyOrderByPublishedDateDesc(key, Pageable.unpaged()).getContent();
            
            for (FormVersionEntity version : versions) {
                version.setDeprecated(deprecated);
                formVersionRepository.save(version);
                count++;
            }
        }
        return count;
    }

    /**
     * 回滚指定表单的版本到指定的版本号。
     *
     * @param key     表单的唯一标识符。
     * @param version 需要回滚到的版本号。
     * @return 回滚后的表单版本模型。
     * @throws NotFoundException 如果指定的版本不存在。
     */
    @Override
    @Transactional
    public FormVersionModel rollbackVersion(String key, Long version) {
        logger.info("Rolling back form version for key: {}, version: {}", key, version);
        FormVersionEntity entity = formVersionRepository.findByKeyAndVersion(key, version)
            .orElseThrow(() -> {
                logger.error("Form version not found for rollback: key={}, version={}", key, version);
                return new NotFoundException("Version not found");
            });
        if(!entity.isDeprecated()){
            logger.info("Form version is not deprecated, no rollback needed: key={}, version={}", key, version);
            return FormVersionModel.fromEntity(entity);
        } else {
            entity.setDeprecated(false);
            FormVersionEntity savedEntity = formVersionRepository.save(entity);
            logger.info("Form version rolled back successfully for key: {}, version: {}", key, version);
            return FormVersionModel.fromEntity(savedEntity);
        }
    }

    /**
     * 根据关键字搜索表单版本，并返回分页结果。
     *
     * @param keyword  搜索关键字，用于匹配表单版本名称（不区分大小写）。
     * @param pageable 分页参数，包含页码、每页大小等信息。
     * @return 包含匹配的表单版本模型的分页结果。
     */
    @Override
    public Page<FormVersionModel> searchVersions(String keyword, Pageable pageable) {
        return formVersionRepository.findByNameContainingIgnoreCase(keyword, pageable)
            .map(FormVersionModel::fromEntity);
    }

    /**
     * 发布表单版本。
     * 将传入的表单版本模型标记为非废弃状态，并保存到数据库。
     *
     * @param model 表单版本模型，包含需要发布的表单版本信息。
     * @return 发布后的表单版本模型。
     */
    @Override
    @Transactional
    public FormVersionModel publishVersion(FormVersionModel model) {
        logger.info("Publishing form version for key: {}, version: {}", model.getKey(), model.getVersion());
        model.setDeprecated(false);
        FormVersionEntity entity = FormVersionModel.toEntity(model);
        FormVersionEntity savedEntity = formVersionRepository.save(entity);
        logger.info("Form version published successfully for key: {}, version: {}", model.getKey(), model.getVersion());
        return FormVersionModel.fromEntity(savedEntity);
    }

    /**
     * 验证给定的键和版本是否唯一。
     *
     * @param key     表单键
     * @param version 表单版本号
     * @return 如果键和版本组合不存在，则返回 true；否则返回 false
     */
    @Override
    public boolean validateUniqueKeyAndVersion(String key, Long version) {
        boolean isUnique = formVersionRepository.findByKeyAndVersion(key, version).isEmpty();
        if (!isUnique) {
            logger.warn("Key and version combination already exists: key={}, version={}", key, version);
        }
        return isUnique;
    }

    /**
     * 获取指定表单键（key）的版本历史记录，按发布时间升序排列。
     *
     * @param key 表单的唯一标识符。
     * @return 包含表单版本模型的列表，按发布时间升序排列。
     */
    @Override
    public List<FormVersionModel> getVersionHistory(String key) {
        // Get descending order and reverse to ascend
        List<FormVersionModel> versions = formVersionRepository
            .findByKeyOrderByPublishedDateDesc(key, Pageable.unpaged())
            .stream()
            .map(FormVersionModel::fromEntity)
            .collect(Collectors.toList());
        
        Collections.reverse(versions);
        return versions;
    }

    @Override
    public List<String> getAllFormKeys() {
        return formVersionRepository.findDistinctKeys();
    }

    @Override
    public long countForms() {
        return getAllFormKeys().size();
    }


}
