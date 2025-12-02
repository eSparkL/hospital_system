// NewsServiceImpl.java
package com.shanzhu.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shanzhu.hospital.entity.po.News;
import com.shanzhu.hospital.entity.vo.NewsPageVo;
import com.shanzhu.hospital.entity.vo.NewsVo;
import com.shanzhu.hospital.mapper.NewsMapper;
import com.shanzhu.hospital.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    private final NewsMapper newsMapper;

    @Override
    public NewsPageVo findNewsPages(Integer pageNum, Integer pageSize, String query, Integer status) {
        Page<News> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<News> queryWrapper = Wrappers.<News>lambdaQuery()
                .like(News::getTitle, query)
                .eq(status != null, News::getStatus, status)
                .orderByDesc(News::getCreateTime);

        IPage<News> iPage = this.page(page, queryWrapper);

        NewsPageVo pageVo = new NewsPageVo();
        pageVo.populatePage(iPage);

        return pageVo;
    }

    @Override
    public NewsPageVo findPublishedNews(Integer pageNum, Integer pageSize, String type) {
        Page<News> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<News> queryWrapper = Wrappers.<News>lambdaQuery()
                .eq(News::getStatus, 1)  // 只查询已发布的
                .eq(type != null && !type.isEmpty(), News::getNewsType, type)
                .orderByDesc(News::getIsTop)
                .orderByDesc(News::getPublishTime);

        IPage<News> iPage = this.page(page, queryWrapper);

        NewsPageVo pageVo = new NewsPageVo();
        pageVo.populatePage(iPage);

        return pageVo;
    }

    @Override
    public NewsVo getNewsDetail(Integer newsId) {
        News news = this.getById(newsId);
        if (news == null) {
            return null;
        }

        // 增加浏览量
        newsMapper.increaseViewCount(newsId);

        NewsVo newsVo = new NewsVo();
        BeanUtils.copyProperties(news, newsVo);

        // 这里可以查询作者名称，暂时返回null
        newsVo.setAuthorName(null);

        return newsVo;
    }

    @Override
    public List<NewsVo> getTopNews(Integer limit) {
        List<News> newsList = newsMapper.selectTopNews(limit);
        return convertToVoList(newsList);
    }

    @Override
    public List<NewsVo> getLatestNews(Integer limit) {
        List<News> newsList = newsMapper.selectLatestNews(limit);
        return convertToVoList(newsList);
    }

    @Override
    public Boolean addNews(News news) {
        LocalDateTime now = LocalDateTime.now();
        news.setCreateTime(now);
        news.setUpdateTime(now);
        news.setViewCount(0);

        // 如果状态是发布，设置发布时间
        if (news.getStatus() == 1) {
            news.setPublishTime(now);
        }

        return this.save(news);
    }



    @Override
    public Boolean updateNews(News news) {
        news.setUpdateTime(LocalDateTime.now());

        // 如果编辑时状态变为发布，更新发布时间
        News existingNews = this.getById(news.getNewsId());
        if (existingNews != null) {
            // 如果需要覆盖发布时间，则更新
            if (news.getStatus() == 1 && (existingNews.getStatus() != 1 || news.getPublishTime() == null)) {
                news.setPublishTime(LocalDateTime.now());
            }
        }

        return this.updateById(news);
    }

    @Override
    public Boolean deleteNews(Integer newsId) {
        return this.removeById(newsId);
    }

    @Override
    public Boolean updateNewsStatus(Integer newsId, Integer status) {
        News news = new News();
        news.setNewsId(newsId);
        news.setStatus(status);
        news.setUpdateTime(LocalDateTime.now());

        // 如果是从非发布状态变为发布状态，更新发布时间
        // 如果需要覆盖时间，则总是更新发布时间
        if (status == 1) {
            news.setPublishTime(LocalDateTime.now());
        }

        return this.updateById(news);
    }

    @Override
    public Boolean updateTopStatus(Integer newsId, Integer isTop) {
        News news = new News();
        news.setNewsId(newsId);
        news.setIsTop(isTop);
        news.setUpdateTime(LocalDateTime.now());

        return this.updateById(news);
    }

    private List<NewsVo> convertToVoList(List<News> newsList) {
        return newsList.stream().map(news -> {
            NewsVo vo = new NewsVo();
            BeanUtils.copyProperties(news, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}