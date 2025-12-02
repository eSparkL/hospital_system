// PageVo.java
package com.shanzhu.hospital.entity.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageVo<T> {
    // 当前页码
    private Long currentPage;
    // 每页条数
    private Long pageSize;
    // 总条数
    private Long total;
    // 总页数
    private Long totalPages;
    // 数据列表
    private List<T> records;

    /**
     * 填充分页数据
     * @param iPage MyBatis Plus 分页对象
     */
    public void populatePage(IPage<T> iPage) {
        this.currentPage = iPage.getCurrent();
        this.pageSize = iPage.getSize();
        this.total = iPage.getTotal();
        this.totalPages = iPage.getPages();
        this.records = iPage.getRecords();
    }

    /**
     * 空参构造方法
     */
    public PageVo() {}

    /**
     * 带参构造方法
     */
    public PageVo(IPage<T> iPage) {
        this.populatePage(iPage);
    }
}