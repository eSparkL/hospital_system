package com.shanzhu.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shanzhu.hospital.entity.po.Drug;
import com.shanzhu.hospital.entity.vo.DrugPageVo;
import com.shanzhu.hospital.mapper.DrugMapper;
import com.shanzhu.hospital.service.DrugService;
import org.springframework.stereotype.Service;

/**
 * 药物 服务层实现类
 */
@Service("drugService")
public class DrugServiceImpl extends ServiceImpl<DrugMapper, Drug> implements DrugService {

    /**
     * 分页查询药物信息
     *
     * @param pageNum  分页页码
     * @param pageSize 分页大小
     * @param query    查询条件
     * @return 药物分页信息
     */
    @Override
    public DrugPageVo findDrugPage(Integer pageNum, Integer pageSize, String query) {
        // 分页条件
        Page<Drug> page = new Page<>(pageNum, pageSize);

        // 查询条件
        LambdaQueryWrapper<Drug> lambdaQuery = Wrappers.<Drug>lambdaQuery()
                .like(Drug::getDrName, query);

        // 分页查询
        IPage<Drug> iPage = this.page(page, lambdaQuery);

        // 组装分页结果
        DrugPageVo pageVo = new DrugPageVo();
        pageVo.populatePage(iPage);

        return pageVo;
    }

    /**
     * 添加药物
     *
     * @param drug 药物信息
     * @return 结果
     */
    @Override
    public Boolean addDrug(Drug drug) {
        // 查询是否存在相同ID的药物
        Drug existDrug = this.getById(drug.getDrId());

        // 如果已存在，返回false
        if (existDrug != null) {
            return Boolean.FALSE;
        }

        // 保存药物信息
        return this.save(drug);
    }

    /**
     * 更新药物信息
     *
     * @param drug 药物信息
     * @return 结果
     */
    @Override
    public Boolean updateDrug(Drug drug) {
        return this.updateById(drug);
    }

    /**
     * 删除药物
     *
     * @param drId 药物ID
     * @return 结果
     */
    @Override
    public Boolean deleteDrug(Integer drId) {
        return this.removeById(drId);
    }
}