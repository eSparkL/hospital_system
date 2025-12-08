// NewsService.java
package com.shanzhu.hospital.service;

import com.shanzhu.hospital.entity.po.News;
import com.shanzhu.hospital.entity.vo.NewsPageVo;
import com.shanzhu.hospital.entity.vo.NewsVo;

import java.util.List;

public interface NewsService {

    // 搜索资讯
    NewsPageVo searchNews(Integer pageNum, Integer pageSize, String keyword);

    // 管理员端：分页查询
    NewsPageVo findNewsPages(Integer pageNum, Integer pageSize, String query, Integer status);

    // 用户端：获取已发布的资讯
    NewsPageVo findPublishedNews(Integer pageNum, Integer pageSize, String type);

    // 获取资讯详情
    NewsVo getNewsDetail(Integer newsId);

    // 获取置顶资讯
    List<NewsVo> getTopNews(Integer limit);

    // 获取最新资讯
    List<NewsVo> getLatestNews(Integer limit);

    // 新增资讯
    Boolean addNews(News news);

    // 修改资讯
    Boolean updateNews(News news);

    // 删除资讯
    Boolean deleteNews(Integer newsId);

    // 发布/下架资讯
    Boolean updateNewsStatus(Integer newsId, Integer status);

    // 置顶/取消置顶
    Boolean updateTopStatus(Integer newsId, Integer isTop);
}