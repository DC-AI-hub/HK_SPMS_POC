package com.spms.backend.service.process;

import com.spms.backend.service.model.idm.UserModel;
import com.spms.backend.service.model.process.ProcessInstanceModel;
import com.spms.backend.service.model.process.TaskModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserInstanceProcessService {

    Page<ProcessInstanceModel> getUserRelatedInstanceService(Long userId, Pageable pageable);

    Page<TaskModel> getAssignedRunningUserTasks(Long userId, Pageable pageable);

    Page<ProcessInstanceModel> getUserRelatedHistoryInstanceService(Long userId, Pageable pageable);

}
