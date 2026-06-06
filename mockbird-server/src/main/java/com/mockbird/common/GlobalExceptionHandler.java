package com.mockbird.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器，统一拦截 Controller 层抛出的异常并转为 Result 格式返回。
 *
 * <h3>工作原理</h3>
 * Spring 的 @ControllerAdvice 会扫描所有带 @ExceptionHandler 的方法，
 * 当 Controller 抛出匹配的异常时，由对应方法接管响应。
 *
 * <h3>异常处理优先级</h3>
 * 精确匹配优先：ResponseStatusException → MethodArgumentNotValidException → Exception（兜底）
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 Controller 主动抛出的业务异常（如 404 资源不存在）。
     * 直接将 ResponseStatusException 中的状态码和原因转换为 Result。
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Result<Void>> handleResponseStatus(ResponseStatusException ex) {
        Result<Void> result = Result.error(ex.getStatus().value(), ex.getReason());
        return new ResponseEntity<>(result, ex.getStatus());
    }

    /**
     * 处理 @Valid 参数校验失败的异常。
     * 收集所有字段的错误消息，用逗号拼接返回。
     * 如：name 为空且 path 为空时，返回 "接口名称不能为空, 接口路径不能为空"。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        Result<Void> result = Result.error(400, message);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    /**
     * 兜底处理所有未预见的异常。
     * 记录完整堆栈日志便于排查，向前端返回通用的 500 错误（不暴露内部细节）。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleGeneral(Exception ex) {
        // 记录完整堆栈，方便线上排查
        log.error("未处理的异常", ex);
        // 不暴露异常细节给前端，返回通用错误信息
        Result<Void> result = Result.error(500, Constants.MSG_SERVER_ERROR);
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
