package com.spms.backend.service.sys;

import com.spms.backend.service.model.SystemConfigHistoryModel;
import com.spms.backend.service.model.SystemConfigModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface SystemConfigService {

    List<SystemConfigModel> getAllSystemConfig();

    SystemConfigModel getSystemConfigByKey(String key);

    List<SystemConfigModel> getSystemConfigByCategory(String category);

    SystemConfigModel updateSystemConfig(String key, SystemConfigModel configModel);

    List<SystemConfigModel> batchUpdateSystemConfig(List<SystemConfigModel> updates);

    SystemConfigModel resetSystemConfig(String key);

    Page<SystemConfigHistoryModel> getSystemConfigHistory(String key, Pageable pageable);

    Map<String, Object> validateSystemConfig(String key, Object value);

    List<String> getSystemConfigCategories();

    byte[] exportSystemConfig(String format);

    Map<String, Object> importSystemConfig(MultipartFile file, boolean overwrite);
}
