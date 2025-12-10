package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.entity.vo.NewsPageVo;
import com.shanzhu.hospital.entity.vo.NewsVo;
import com.shanzhu.hospital.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/search")
    public R<NewsPageVo> searchNews(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "20") Integer pageSize) {
        return R.ok(newsService.searchNews(pageNum, pageSize, keyword));
    }

    /**
     * 获取已发布的资讯列表（分页）- 公开访问
     */
    @GetMapping("/published")
    public R<NewsPageVo> findPublishedNews(
            @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "type", required = false) String type) {
        return R.ok(newsService.findPublishedNews(pageNum, pageSize, type));
    }

    /**
     * 获取资讯详情 - 公开访问
     */
    @GetMapping("/detail/{newsId}")
    public R<NewsVo> getNewsDetail(@PathVariable("newsId") Integer newsId) {
        NewsVo newsVo = newsService.getNewsDetail(newsId);
        if (newsVo == null) {
            return R.error("资讯不存在");
        }
        return R.ok(newsVo);
    }

    /**
     * 获取置顶资讯 - 公开访问
     */
    @GetMapping("/top")
    public R<List<NewsVo>> getTopNews(
            @RequestParam(value = "limit", defaultValue = "3") Integer limit) {
        return R.ok(newsService.getTopNews(limit));
    }

    /**
     * 获取最新资讯 - 公开访问
     */
    @GetMapping("/latest")
    public R<List<NewsVo>> getLatestNews(
            @RequestParam(value = "limit", defaultValue = "5") Integer limit) {
        return R.ok(newsService.getLatestNews(limit));
    }
}