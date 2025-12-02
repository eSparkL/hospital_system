// NewsVo.java
package com.shanzhu.hospital.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewsVo {
    private Integer newsId;
    private String title;
    private String content;
    private String coverImage;
    private String newsType;
    private Integer status;
    private String authorName;
    private Integer viewCount;
    private Integer isTop;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 摘要（用于列表展示，截取前100字）
    public String getSummary() {
        if (content == null || content.length() <= 100) {
            return content;
        }
        return content.substring(0, 100) + "...";
    }
}

