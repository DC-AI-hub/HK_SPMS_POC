package com.spms.backend.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一API响应封装类
 * 符合cursor规则要求的标准响应格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    /**
     * 响应结果状态：SUCCESS 或 ERROR
     */
    private String result;
    
    /**
     * 响应消息：成功或错误信息
     */
    private String message;
    
    /**
     * 响应数据：来自service层的返回对象（成功时）
     */
    private T data;
    
    /**
     * 创建成功响应
     * @param message 成功消息
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("SUCCESS", message, data);
    }
    
    /**
     * 创建成功响应（无数据）
     * @param message 成功消息
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>("SUCCESS", message, null);
    }
    
    /**
     * 创建错误响应
     * @param message 错误消息
     * @return 错误响应对象
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("ERROR", message, null);
    }
    
    /**
     * 创建错误响应（带状态码）
     * @param statusCode 状态码
     * @param message 错误消息
     * @return 错误响应对象
     */
    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return new ApiResponse<>("ERROR", statusCode + ": " + message, null);
    }
}
