package com.spms.backend.service.project;

import com.spms.backend.controller.dto.project.TaskDTO;
import com.spms.backend.controller.dto.project.TaskCreateDTO;
import com.spms.backend.controller.dto.project.TaskOptionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 任务服务接口
 * 定义任务相关的业务逻辑方法
 */
public interface TaskService {

    /**
     * 创建新任务
     * @param createDTO 任务创建数据
     * @return 创建的任务DTO
     */
    TaskDTO createTask(TaskCreateDTO createDTO);

    /**
     * 根据ID获取任务
     * @param id 任务ID
     * @return 任务DTO
     */
    TaskDTO getTaskById(Long id);

    /**
     * 根据任务编号获取任务
     * @param taskNumber 任务编号
     * @return 任务DTO
     */
    TaskDTO getTaskByNumber(String taskNumber);

    /**
     * 更新任务
     * @param id 任务ID
     * @param taskDTO 更新的任务数据
     * @return 更新后的任务DTO
     */
    TaskDTO updateTask(Long id, TaskDTO taskDTO);

    /**
     * 删除任务
     * @param id 任务ID
     */
    void deleteTask(Long id);

    /**
     * 分页查询任务
     * @param pageable 分页参数
     * @return 任务分页数据
     */
    Page<TaskDTO> getTasks(Pageable pageable);

    /**
     * 根据项目ID查询任务
     * @param projectId 项目ID
     * @return 任务列表
     */
    List<TaskDTO> getTasksByProjectId(Long projectId);

    /**
     * 根据项目代码查询任务
     * @param projectCode 项目代码
     * @return 任务列表
     */
    List<TaskDTO> getTasksByProjectCode(String projectCode);

    /**
     * 根据任务名称模糊查询
     * @param taskName 任务名称关键字
     * @return 任务列表
     */
    List<TaskDTO> searchTasksByName(String taskName);

    /**
     * 获取项目的任务选项（用于下拉选择）
     * @param projectId 项目ID
     * @return 任务选项列表
     */
    List<TaskOptionDTO> getTaskOptionsByProjectId(Long projectId);

    /**
     * 检查任务编号是否存在
     * @param taskNumber 任务编号
     * @return 是否存在
     */
    boolean existsByTaskNumber(String taskNumber);

    /**
     * 获取项目的任务数量
     * @param projectId 项目ID
     * @return 任务数量
     */
    Long getTaskCountByProjectId(Long projectId);
}
