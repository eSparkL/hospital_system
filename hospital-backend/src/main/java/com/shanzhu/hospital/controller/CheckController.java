package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.entity.po.Checks;
import com.shanzhu.hospital.entity.vo.ChecksPageVo;
import com.shanzhu.hospital.service.CheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 检查项目 控制层
 *
 */
@RestController
@RequestMapping("check")
@RequiredArgsConstructor
@Tag(name = "检查项目", description = "检查项目相关接口")
public class CheckController {

    private final CheckService checkService;

    /**
     * 查询检查项目 分页
     *
     * @param pageNum  分页页面
     * @param pageSize 分页大小
     * @param query    查询条件
     * @return 检查项目
     */
    @RequestMapping("findAllChecks")
    @Operation(summary = "分页查询检查项目", description = "根据查询条件分页查询检查项目信息")
    public R<ChecksPageVo> findChecksPage(
            @Parameter(description = "页码") @RequestParam(value = "pageNumber") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "size") Integer pageSize,
            @Parameter(description = "查询关键字") @RequestParam(value = "query") String query

    ) {
        return R.ok(checkService.findChecksPage(pageNum, pageSize, query));
    }

    /**
     * 通过id查询检查项目
     *
     * @param chId 检查项目id
     * @return 检查项目
     */
    @RequestMapping("findCheck")
    @Operation(summary = "根据ID查询检查项目", description = "通过检查项目ID查询检查项目详细信息")
    public R<Checks> findCheck(@Parameter(description = "检查项目ID") Integer chId) {
        return R.ok(checkService.findCheck(chId));
    }

    /**
     * 添加检查项目
     *
     * @param checks 检查项目
     * @return 结果
     */
    @RequestMapping("addCheck")
    @Operation(summary = "添加检查项目", description = "新增检查项目信息")
    public R<Boolean> addCheck(@Parameter(description = "检查项目信息对象") Checks checks) {
        if (BooleanUtils.isTrue(checkService.addCheck(checks))) {
            return R.ok("增加检查项成功");
        }

        return R.error("增加检查项失败！账号或已被占用");
    }

    /**
     * 更新检查项目
     *
     * @param checks 检查项目信息
     * @return 结果
     */
    @RequestMapping("modifyCheck")
    @Operation(summary = "更新检查项目", description = "更新检查项目信息")
    public R<Boolean> updateCheck(@Parameter(description = "检查项目信息对象") Checks checks) {
        if (BooleanUtils.isTrue(checkService.updateChecks(checks))) {
            return R.ok("修改检查项目成功");
        }

        return R.error("修改检查项目失败");
    }

    /**
     * 删除检查项目
     *
     * @param chId 检查项目id
     * @return 结果
     */
    @RequestMapping("deleteCheck")
    @Operation(summary = "删除检查项目", description = "根据检查项目ID删除检查项目信息")
    public R<Boolean> deleteCheck(@Parameter(description = "检查项目ID") @RequestParam(value = "chId") Integer chId) {
        if (BooleanUtils.isTrue(checkService.deleteChecks(chId))) {
            return R.ok("删除检查项目成功");
        }

        return R.error("删除检查项目失败");
    }

}
