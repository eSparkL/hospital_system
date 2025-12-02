// NewsMapper.java
package com.shanzhu.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shanzhu.hospital.entity.po.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NewsMapper extends BaseMapper<News> {

    // 增加浏览量
    @Update("UPDATE news SET view_count = view_count + 1 WHERE news_id = #{newsId}")
    void increaseViewCount(@Param("newsId") Integer newsId);

    // 获取置顶资讯
    List<News> selectTopNews(@Param("limit") Integer limit);

    // 获取最新资讯
    List<News> selectLatestNews(@Param("limit") Integer limit);
}