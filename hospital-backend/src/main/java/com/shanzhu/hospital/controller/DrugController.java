package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.entity.po.Drug;
import com.shanzhu.hospital.entity.vo.DrugPageVo;
import com.shanzhu.hospital.service.DrugService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 药物管理 控制层
 */
@RestController
@RequestMapping("drug")
@RequiredArgsConstructor
@Tag(name = "药物管理", description = "药物相关接口")
public class DrugController {

    private final DrugService drugService;

    /**
     * 分页查询药物信息
     *
     * @param pageNum  分页页码
     * @param pageSize 分页大小
     * @param query    查询条件
     * @return 药物分页数据
     */
    @RequestMapping("findAllDrugs")
    @Operation(summary = "分页查询药物信息", description = "根据查询条件分页查询所有药物信息")
    public R<DrugPageVo> findDrugPage(
            @RequestParam(value = "pageNumber") Integer pageNum,
            @RequestParam(value = "size") Integer pageSize,
            @RequestParam(value = "query") String query
    ) {
        return R.ok(drugService.findDrugPage(pageNum, pageSize, query));
    }

    /**
     * 根据ID查询药物
     *
     * @param drId 药物ID
     * @return 药物信息
     */
    @RequestMapping("findDrug")
    @Operation(summary = "根据ID查询药物", description = "通过药物ID查询药物详细信息")
    public R<Drug> findDrug(@RequestParam(value = "drId") Integer drId) {
        return R.ok(drugService.getById(drId));
    }

    /**
     * 添加药物
     *
     * @param drug 药物信息
     * @return 结果
     */
    @RequestMapping("addDrug")
    @Operation(summary = "添加药物", description = "新增药物信息")
    public R<Boolean> addDrug(Drug drug) {
        if (BooleanUtils.isTrue(drugService.addDrug(drug))) {
            return R.ok("添加药物成功");
        }

        return R.error("添加药物失败，药物ID可能已存在");
    }

    /**
     * 更新药物信息
     *
     * @param drug 药物信息
     * @return 结果
     */
    @RequestMapping("modifyDrug")
    @Operation(summary = "更新药物信息", description = "更新药物信息")
    public R<Boolean> updateDrug(Drug drug) {
        if (BooleanUtils.isTrue(drugService.updateDrug(drug))) {
            return R.ok("修改药物成功");
        }

        return R.error("修改药物失败");
    }

    /**
     * 删除药物
     *
     * @param drId 药物ID
     * @return 结果
     */
    @RequestMapping("deleteDrug")
    @Operation(summary = "删除药物", description = "根据药物ID删除药物信息")
    public R<Boolean> deleteDrug(@RequestParam(value = "drId") Integer drId) {
        if (BooleanUtils.isTrue(drugService.deleteDrug(drId))) {
            return R.ok("删除药物成功");
        }

        return R.error("删除药物失败");
    }
}