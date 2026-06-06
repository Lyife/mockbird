package com.mockbird.common;

/**
 * 项目级共享常量，消除代码中的魔法值。
 */
public final class Constants {

    private Constants() {
    }

    /** 业务成功状态码 */
    public static final int CODE_SUCCESS = 200;
    public static final String MSG_SUCCESS = "success";

    /** 错误消息模板 */
    public static final String MSG_PROJECT_NOT_FOUND = "项目不存在: ";
    public static final String MSG_INTERFACE_NOT_FOUND = "接口不存在: ";
    public static final String MSG_MOCK_RULE_NOT_FOUND = "Mock 规则不存在: ";
    public static final String MSG_FILE_EMPTY = "文件不能为空";
    public static final String MSG_UNSUPPORTED_FORMAT = "仅支持 JSON/YAML 格式的文件";
    public static final String MSG_SERVER_ERROR = "服务器内部错误";

    /** Content-Type */
    public static final String CONTENT_TYPE_JSON_UTF8 = "application/json;charset=UTF-8";
    public static final String CHARSET_UTF8 = "UTF-8";

    /** 分页默认值 */
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** Mock 规则默认值 */
    public static final int DEFAULT_ENABLED = 1;
    public static final int DEFAULT_STATUS_CODE = 200;
    public static final int DEFAULT_DELAY_MS = 0;

    /** Mock 请求 */
    public static final String MOCK_PATH_PREFIX = "/mock";
    public static final String TEMPLATE_PREFIX_PARAM = "request.param.";
    public static final String TEMPLATE_PREFIX_HEADER = "request.header.";
    public static final String TEMPLATE_KEY_BODY = "request.body";
    public static final String TEMPLATE_PREFIX_PATH = "path.";

    /** 文件上传 */
    public static final String MINIO_OBJECT_PREFIX = "openapi/";
    public static final String MINIO_UPLOAD_CONTENT_TYPE = "application/octet-stream";
    public static final int MINIO_PART_SIZE = -1;
}
