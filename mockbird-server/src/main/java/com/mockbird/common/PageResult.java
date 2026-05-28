package com.mockbird.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 分页响应包装类，用于统一分页查询的返回格式。
 * 配合 MyBatis-Plus 的 Page 对象使用。
 *
 * @param <T> 分页列表中的数据类型
 */
@Data
public class PageResult<T> {
    /** 当前页的数据列表 */
    private List<T> list;
    /** 总记录数 */
    private long total;
    /** 当前页码 */
    private int page;
    /** 每页大小 */
    private int size;

    /**
     * 从 MyBatis-Plus 的 Page 对象构建分页结果。
     *
     * @param mpPage  MyBatis-Plus 分页对象（包含 total/current/size）
     * @param records 转换后的 VO 列表
     * @param <T>     数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(Page<?> mpPage, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.list = records;
        result.total = mpPage.getTotal();
        result.page = (int) mpPage.getCurrent();
        result.size = (int) mpPage.getSize();
        return result;
    }
}
