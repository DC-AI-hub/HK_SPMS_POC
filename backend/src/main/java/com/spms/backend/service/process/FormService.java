package com.spms.backend.service.process;

import com.spms.backend.service.model.process.FormDefinitionModel;
import com.spms.backend.service.model.process.FormVersionModel;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FormService {
    /**
     * 获取指定表单键的最新版本。
     *
     * @param key 表单的唯一标识符
     * @return 最新版本的表单模型
     */
    FormVersionModel getLatestVersion(String key);

    /**
     * 批量更新表单版本的废弃状态。
     *
     * @param keys 表单键列表
     * @param deprecated 是否废弃
     * @return 更新成功的数量
     */
    int batchUpdateDeprecatedStatus(List<String> keys, boolean deprecated);

    /**
     * 回滚到指定版本的表单。
     *
     * @param key 表单的唯一标识符
     * @param version 目标版本号
     * @return 回滚后的表单模型
     */
    FormVersionModel rollbackVersion(String key, Long version);

    /**
     * 模糊查询表单版本。
     *
     * @param keyword 关键字（匹配名称或描述）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<FormVersionModel> searchVersions(String keyword, Pageable pageable);

    /**
     * 发布新版本的表单。
     *
     * @param model 表单模型
     * @return 发布后的表单模型
     */
    FormVersionModel publishVersion(FormVersionModel model);

    /**
     * 校验表单键和版本号的唯一性。
     *
     * @param key 表单的唯一标识符
     * @param version 版本号
     * @return 是否唯一
     */
    boolean validateUniqueKeyAndVersion(String key, Long version);

    /**
     * 获取表单的版本历史记录。
     *
     * @param key 表单的唯一标识符
     * @return 版本历史列表
     */
    List<FormVersionModel> getVersionHistory(String key);
        /**
     * 创建新的表单版本。
     *
     * @param key 表单的唯一标识符
     * @param model 表单定义模型
     * @return 创建的表单版本模型
     */
    FormVersionModel createFormVersion(String key, FormDefinitionModel model);
        /**
     * 获取指定表单键和版本号的表单版本。
     *
     * @param key 表单的唯一标识符
     * @param version 版本号
     * @return 匹配的表单版本模型
     */
    FormVersionModel getVersion(String key, String version);
        /**
     * 获取指定表单键的所有版本列表。
     *
     * @param key 表单的唯一标识符
     * @return 表单版本模型列表
     */
    List<FormVersionModel> listVersions(String key);
        /**
     * 废弃指定表单键和版本号的表单版本。
     *
     * @param key 表单的唯一标识符
     * @param version 版本号
     */
    void deprecateVersion(String key, String version);

    /**
     * 获取所有表单键
     *
     * @return 表单键列表
     */
    List<String> getAllFormKeys();
    
    /**
     * Count all forms
     * @return the total number of forms
     */
    long countForms();
}
