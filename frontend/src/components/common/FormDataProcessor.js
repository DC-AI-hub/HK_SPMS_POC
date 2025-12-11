/**
 * 统一的表单数据处理器
 * 用于标准化不同类型表单的数据处理逻辑
 */
export class FormDataProcessor {

  /**
   * 表单类型枚举
   */
  static FormType = {
    LEAVE: 'LEAVE',
    OTHER: 'OTHER'
  };

  /**
   * 处理请假表单数据
   * @param {Object} unifiedData - 统一格式的表单数据
   * @param {boolean} isRejection - 是否为拒绝状态
   * @returns {Object} 处理后的表单数据
   */
  static processLeaveData(unifiedData, isRejection = false) {
    if (!unifiedData || unifiedData.formType !== this.FormType.LEAVE) {
      return unifiedData;
    }

    return {
      ...unifiedData.formData,
      formType: unifiedData.formType,
      submited: isRejection ? "false" : "true"
    };
  }

  /**
   * 处理通用表单数据
   * @param {Object} unifiedData - 统一格式的表单数据
   * @param {boolean} isRejection - 是否为拒绝状态
   * @returns {Object} 处理后的表单数据
   */
  static processGenericData(unifiedData, isRejection = false) {
    if (!unifiedData) {
      return {};
    }

    return {
      ...unifiedData.formData,
      formType: unifiedData.formType || this.FormType.OTHER,
      submited: isRejection ? "false" : "true"
    };
  }

  /**
   * 统一的表单数据处理入口
   * @param {Object} contextData - 流程上下文数据
   * @returns {Object} 处理后的表单数据
   */
  static processFormData(contextData) {
    if (!contextData || !contextData.formData) {
      console.warn('No form data found in context');
      return {};
    }

    // 检查是否为新的统一格式
    if (contextData.formData.unifiedFormData) {
      try {
        const unifiedData = typeof contextData.formData.unifiedFormData === 'string'
          ? JSON.parse(contextData.formData.unifiedFormData)
          : contextData.formData.unifiedFormData;

        const isRejection = !!contextData.rejectionReason;

        switch (unifiedData.formType) {
          case this.FormType.LEAVE:
            return this.processLeaveData(unifiedData, isRejection);
          default:
            return this.processGenericData(unifiedData, isRejection);
        }
      } catch (error) {
        console.error('Failed to process unified form data:', error);
        return this.processLegacyData(contextData);
      }
    } else {
      // 处理旧格式数据
      return this.processLegacyData(contextData);
    }
  }

  /**
   * 处理旧格式的表单数据（向后兼容）
   * @param {Object} contextData - 流程上下文数据
   * @returns {Object} 处理后的表单数据
   */
  static processLegacyData(contextData) {
    console.warn('Processing legacy form data format');

    const isRejection = !!contextData.rejectionReason;

    // 处理旧格式数据
    // 相关格式已移除

    // 处理其他旧格式数据
    return {
      ...contextData.formData,
      submited: isRejection ? "false" : "true"
    };
  }

  /**
   * 获取表单的展示信息
   * @param {Object} unifiedData - 统一格式的表单数据
   * @returns {Object} 展示信息
   */
  static getDisplayInfo(unifiedData) {
    const displayInfo = {
      title: '审批请求',
      requester: '未知用户',
      details: []
    };

    if (!unifiedData) {
      return displayInfo;
    }

    switch (unifiedData.formType) {
      case this.FormType.LEAVE:
        displayInfo.title = '请假申请审批';
        displayInfo.requester = unifiedData.employeeName || '未知用户';
        displayInfo.details = [
          `请假类型: ${unifiedData.leaveType || '未知'}`,
          `开始日期: ${unifiedData.startDate || '未知'}`,
          `结束日期: ${unifiedData.endDate || '未知'}`,
          `请假原因: ${unifiedData.reason || '未填写'}`
        ];
        break;

      default:
        displayInfo.title = '审批请求';
        displayInfo.requester = '未知用户';
        displayInfo.details = ['通用审批请求'];
        break;
    }

    return displayInfo;
  }

  /**
   * 从JSON对象中提取值
   * @param {Object|string} jsonData - JSON数据
   * @param {string} path - JSON路径
   * @returns {string} 提取的值
   */
  static extractJsonValue(jsonData, path) {
    try {
      if (typeof jsonData === 'string') {
        jsonData = JSON.parse(jsonData);
      }

      const keys = path.replace(/^\//, '').split('/');
      let value = jsonData;

      for (const key of keys) {
        if (value && typeof value === 'object' && key in value) {
          value = value[key];
        } else {
          return '未知';
        }
      }

      return value || '未知';
    } catch (error) {
      return '未知';
    }
  }
}

export default FormDataProcessor;
