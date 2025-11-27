package com.shanzhu.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shanzhu.hospital.entity.po.Drug;
import com.shanzhu.hospital.entity.vo.DrugPageVo;

/**
 * 药物 服务层接口
 */
public interface DrugService extends IService<Drug> {

    /**
     * 分页查询药物信息
     *
     * @param pageNum  分页页码
     * @param pageSize 分页大小
     * @param query    查询条件
     * @return 药物分页信息
     */
    DrugPageVo findDrugPage(Integer pageNum, Integer pageSize, String query);

    /**
     * 添加药物
     *
     * @param drug 药物信息
     * @return 结果
     */
    Boolean addDrug(Drug drug);

    /**
     * 更新药物信息
     *
     * @param drug 药物信息
     * @return 结果
     */
    Boolean updateDrug(Drug drug);

    /**
     * 删除药物
     *
     * @param drId 药物ID
     * @return 结果
     */
    Boolean deleteDrug(Integer drId);
}