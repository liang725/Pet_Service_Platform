package com.ddliang.backend.common;

import java.util.List;

/**
 * 分页结果类
 */
public class PageResult<T> {
    private List<T> list;           // 当前页数据列表
    private long total;             // 总记录数
    private int page;               // 当前页码
    private int pageSize;           // 每页大小
    private int totalPages;         // 总页数

    // 构造方法
    public PageResult() {}

    public PageResult(List<T> list, long total, int page, int pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        if (pageSize > 0) {
            this.totalPages = (int) Math.ceil((double) total / pageSize);
        } else {
            this.totalPages = 0;
        }
    }

    public PageResult(List<T> list, long total, int page, int pageSize, int totalPages) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
    }

    // 创建一个成功的结果
    public static <T> PageResult<T> of(List<T> list, long total, int page, int pageSize) {
        return new PageResult<>(list, total, page, pageSize);
    }

    // 创建一个空结果
    public static <T> PageResult<T> empty(int page, int pageSize) {
        return new PageResult<>(List.of(), 0, page, pageSize, 0);
    }

    // Getters and Setters
    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
        if (this.pageSize > 0) {
            this.totalPages = (int) Math.ceil((double) total / this.pageSize);
        }
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        if (pageSize > 0 && this.total > 0) {
            this.totalPages = (int) Math.ceil((double) this.total / pageSize);
        }
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    // 判断是否有上一页
    public boolean hasPrevious() {
        return page > 1;
    }

    // 判断是否有下一页
    public boolean hasNext() {
        return page < totalPages;
    }

    // 获取起始索引（用于数据库查询）
    public int getOffset() {
        if (page <= 0) {
            return 0;
        }
        return (page - 1) * pageSize;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "listSize=" + (list != null ? list.size() : 0) +
                ", total=" + total +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", totalPages=" + totalPages +
                '}';
    }
}