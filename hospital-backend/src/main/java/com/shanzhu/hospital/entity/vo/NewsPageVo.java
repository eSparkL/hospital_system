// NewsPageVo.java
package com.shanzhu.hospital.entity.vo;

import com.shanzhu.hospital.entity.po.News;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NewsPageVo extends PageVo<News> {
    // 这里可以添加资讯特有的分页属性，如果有的话
    // 例如：按类型统计的数量等

    public NewsPageVo() {
        super();
    }
}