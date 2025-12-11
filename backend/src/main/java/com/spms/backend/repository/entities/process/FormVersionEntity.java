package com.spms.backend.repository.entities.process;

import com.spms.backend.repository.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 表单版本实体类，用于存储表单的版本信息。
 * 每个表单版本包含名称、描述、唯一键、版本号、发布时间、表单定义等属性。
 *
 * @Entity 表示该类是一个JPA实体，映射到数据库表。
 * @Table 指定数据库表名为 "spms_form_version"，并定义唯一约束确保键和版本的唯一性。
 *
 *        继承自 BaseEntity，包含基础的实体字段（如ID、创建时间等）。
 *
 * @Column 注解用于定义字段的数据库列属性：
 *         - name: 表单版本名称
 *         - description: 表单版本描述
 *         - key: 表单的唯一键，不可为空
 *         - version: 表单版本号，不可为空
 *         - publishedDate: 表单发布时间，不可为空
 *         - formDefinition: 表单定义内容，使用 @Lob 注解存储大文本
 *         - deprecated: 标记表单版本是否已弃用，不可更新
 *         - deploymentId: 表单部署ID
 *
 *         通过 @Setter 和 @Getter 注解自动生成字段的getter和setter方法。
 */
@Setter
@Getter
@Entity
@Table(name = "spms_form_version",
        uniqueConstraints = {
                @UniqueConstraint(name = "spms_uq_form_version_key", columnNames = {"key", "version"})
        }
)
public class FormVersionEntity extends BaseEntity {

    @Column
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private Long version;

    @Column(nullable = false)
    private Long publishedDate;

    @Lob
    @Column
    private String formDefinition;

    @Column(nullable = false ,updatable = false)
    private boolean deprecated;

    @Column
    private String deploymentId;

}
