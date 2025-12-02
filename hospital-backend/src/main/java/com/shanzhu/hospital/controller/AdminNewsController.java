// 管理员端Controller - AdminNewsController.java
package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.entity.po.News;
import com.shanzhu.hospital.entity.vo.NewsPageVo;
import com.shanzhu.hospital.entity.vo.NewsVo;
import com.shanzhu.hospital.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/news")
@RequiredArgsConstructor
public class AdminNewsController {

    private final NewsService newsService;

    /**
     * 分页查询所有资讯（管理员端）
     */
    @GetMapping("/list")
    public R<NewsPageVo> findNewsPages(
            @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "query", defaultValue = "") String query,
            @RequestParam(value = "status", required = false) Integer status) {
        return R.ok(newsService.findNewsPages(pageNum, pageSize, query, status));
    }

    /**
     * 获取资讯详情
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
     * 新增资讯
     */
    @PostMapping("/add")
    public R addNews(@RequestBody News news) {
        if (newsService.addNews(news)) {
            return R.ok("添加成功");
        }
        return R.error("添加失败");
    }

    /**
     * 修改资讯
     */
    @PostMapping("/update")
    public R updateNews(@RequestBody News news) {
        if (newsService.updateNews(news)) {
            return R.ok("修改成功");
        }
        return R.error("修改失败");
    }

    /**
     * 删除资讯
     */
    @PostMapping("/delete")
    public R deleteNews(@RequestParam("newsId") Integer newsId) {
        if (newsService.deleteNews(newsId)) {
            return R.ok("删除成功");
        }
        return R.error("删除失败");
    }

    /**
     * 发布/下架资讯
     */
    @PostMapping("/updateStatus")
    public R updateNewsStatus(
            @RequestParam("newsId") Integer newsId,
            @RequestParam("status") Integer status) {
        if (newsService.updateNewsStatus(newsId, status)) {
            String msg = status == 1 ? "发布成功" : "下架成功";
            return R.ok(msg);
        }
        return R.error("操作失败");
    }

    /**
     * 置顶/取消置顶
     */
    @PostMapping("/updateTop")
    public R updateTopStatus(
            @RequestParam("newsId") Integer newsId,
            @RequestParam("isTop") Integer isTop) {
        if (newsService.updateTopStatus(newsId, isTop)) {
            String msg = isTop == 1 ? "置顶成功" : "取消置顶成功";
            return R.ok(msg);
        }
        return R.error("操作失败");
    }
}