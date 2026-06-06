package com.mockbird.common;

import lombok.Data;

/**
 * 统一 API 响应体，所有 Controller 的返回值都用此类包装。
 * 前端通过 {@code code} 判断请求是否成功，从 {@code data} 取业务数据。
 *
 * @param <T> 业务数据的类型
 */
@Data
public class Result<T> {

    /** 业务状态码：200 成功，400 参数校验失败，404 资源不存在，500 服务器错误 */
    private int code;
    /** 错误信息或成功提示 */
    private String message;
    /** 业务数据，错误时为 null */
    private T data;

    /**
     * 构建成功响应。
     *
     * @param data 业务数据
     * @return code=200 的 Result
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = Constants.CODE_SUCCESS;
        result.message = Constants.MSG_SUCCESS;
        result.data = data;
        return result;
    }

    /**
     * 构建错误响应。
     *
     * @param code    业务状态码（通常对应 HTTP 状态码）
     * @param message 错误描述
     * @return 不含 data 的 Result
     */
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        return result;
    }
}
