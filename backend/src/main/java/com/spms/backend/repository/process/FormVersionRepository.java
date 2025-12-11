package com.spms.backend.repository.process;

import com.spms.backend.repository.entities.process.FormVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

/**
 * 表单版本数据访问接口，提供对表单版本实体的CRUD和自定义查询操作。
 * 继承自JpaRepository，支持标准JPA方法，同时扩展了针对表单版本的特殊查询需求。
 * 主要功能包括：
 * - 按表单键和版本号查询
 * - 查找最新版本
 * - 按废弃状态筛选
 * - 按发布时间范围查询
 */
public interface FormVersionRepository extends JpaRepository<FormVersionEntity, Long> {
    /**
     * 根据指定的key查询表单版本列表，并按发布时间降序排序。
     *
     * @param key 表单的唯一标识符
     * @param pageable 分页参数
     * @return 返回按发布时间降序排列的表单版本分页结果
     */
    Page<FormVersionEntity> findByKeyOrderByPublishedDateDesc(String key, Pageable pageable);

    /**
     * 根据表单的唯一标识符和版本号查找对应的表单版本实体。
     *
     * @param key     表单的唯一标识符
     * @param version 表单的版本号
     * @return 返回一个包含表单版本实体的Optional对象，如果未找到则返回空Optional
     */
    Optional<FormVersionEntity> findByKeyAndVersion(String key, Long version);

    /**
     * 根据部署ID查找表单版本实体。
     *
     * @param deploymentId 部署ID
     * @return 包含表单版本实体的Optional对象，如果未找到则返回空Optional
     */
    Optional<FormVersionEntity> findByDeploymentId(String deploymentId);

    /**
     * 根据废弃状态查询表单版本列表。
     *
     * @param deprecated 是否废弃，true 表示废弃，false 表示未废弃
     * @param pageable 分页参数
     * @return 符合废弃状态的表单版本实体分页结果
     */
    Page<FormVersionEntity> findByDeprecated(boolean deprecated, Pageable pageable);

    /**
     * 根据发布时间查找晚于指定日期的表单版本列表。
     *
     * @param date 指定的日期时间戳（毫秒级）
     * @param pageable 分页参数
     * @return 返回发布时间晚于指定日期的表单版本实体分页结果
     */
    Page<FormVersionEntity> findByPublishedDateAfter(Long date, Pageable pageable);

    /**
     * 根据指定的key查找最新的表单版本。
     *
     * @param key 表单的唯一标识符
     * @return 返回一个Optional包装的表单版本实体，按版本号降序排列后的第一个结果
     */
    Optional<FormVersionEntity> findFirstByKeyOrderByVersionDesc(String key);

    /**
     * 根据指定的键统计数量。
     *
     * @param key 用于统计的键
     * @return 返回与键关联的数量
     */
    Long countByKey(String key);

    /**
     * 按名称模糊查询表单版本（支持分页）。
     *
     * @param name 表单名称（模糊匹配）
     * @param pageable 分页参数
     * @return 返回匹配的表单版本分页结果
     */
    Page<FormVersionEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * 按描述模糊查询表单版本（支持分页）。
     *
     * @param description 表单描述（模糊匹配）
     * @param pageable 分页参数
     * @return 返回匹配的表单版本分页结果
     */
    Page<FormVersionEntity> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    /**
     * 按发布时间范围查询表单版本（支持分页）。
     *
     * @param startDate 开始时间戳（毫秒级）
     * @param endDate 结束时间戳（毫秒级）
     * @param pageable 分页参数
     * @return 返回发布时间在指定范围内的表单版本分页结果
     */
    Page<FormVersionEntity> findByPublishedDateBetween(Long startDate, Long endDate, Pageable pageable);

    /**
     * 按表单定义内容查询表单版本（支持分页）。
     *
     * @param definition 表单定义内容（模糊匹配）
     * @param pageable 分页参数
     * @return 返回匹配的表单版本分页结果
     */
    Page<FormVersionEntity> findByFormDefinitionContaining(String definition, Pageable pageable);

    /**
     * 按表单键和废弃状态查询表单版本（支持分页）。
     *
     * @param key 表单的唯一标识符
     * @param deprecated 是否废弃
     * @param pageable 分页参数
     * @return 返回匹配的表单版本分页结果
     */
    Page<FormVersionEntity> findByKeyAndDeprecated(String key, boolean deprecated, Pageable pageable);

    /**
     * 按版本号范围查询表单版本（支持分页）。
     *
     * @param key 表单的唯一标识符
     * @param startVersion 起始版本号
     * @param endVersion 结束版本号
     * @param pageable 分页参数
     * @return 返回版本号在指定范围内的表单版本分页结果
     */
    Page<FormVersionEntity> findByKeyAndVersionBetween(String key, Long startVersion, Long endVersion, Pageable pageable);

    /**
     * 统计指定表单键的废弃和非废弃版本数量。
     *
     * @param key 表单的唯一标识符
     * @param deprecated 是否废弃
     * @return 返回匹配的表单版本数量
     */
    Long countByKeyAndDeprecated(String key, boolean deprecated);

    /**
     * 查找所有不同的表单键
     *
     * @return 返回不同的表单键列表
     */
    @Query("SELECT DISTINCT f.key FROM FormVersionEntity f")
    List<String> findDistinctKeys();
}
