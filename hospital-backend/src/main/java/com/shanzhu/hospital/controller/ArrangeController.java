package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.entity.po.Arrange;
import com.shanzhu.hospital.service.ArrangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 排班相关 控制层
 *
 */
@RestController
@RequestMapping("/arrange")
@RequiredArgsConstructor
@Tag(name = "排班接口", description = "排班相关操作接口")
public class ArrangeController {

    private final ArrangeService arrangeService;

    /**
     * 通过日期查询排班
     *
     * @param arTime   排班时间
     * @param dSection 科室
     * @return 排班信息
     */
    @RequestMapping("findByTime")
    @Operation(summary = "根据日期和科室查询排班", description = "根据指定日期和科室查询医生排班信息")
    public R<List<Arrange>> findArrange(
            @Parameter(description = "排班日期") @RequestParam(value = "arTime") String arTime,
            @Parameter(description = "科室名称") @RequestParam(value = "dSection") String dSection
    ) {
        return R.ok(arrangeService.findArrange(arTime, dSection));
    }

    /**
     * 添加排班
     *
     * @param arrange 排班信息
     * @return 结果
     */
    @RequestMapping("addArrange")
    @Operation(summary = "添加排班", description = "为医生添加排班信息")
    public R<Boolean> addArrange(@Parameter(description = "排班信息对象") Arrange arrange) {
        if (BooleanUtils.isTrue(arrangeService.addArrange(arrange))) {
            return R.ok("添加排班成功");
        }

        return R.error("该医生该日已排班");
    }

    /**
     * 删除排班
     *
     * @param arId 排班id
     * @return 结果
     */
    @RequestMapping("deleteArrange")
    public R<Boolean> deleteArrange(String arId) {
        if (BooleanUtils.isTrue(arrangeService.deleteArrange(arId))){
            return R.ok("删除排班成功");
        }

        return R.error("排班信息不存在");
    }

}
