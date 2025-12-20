package com.shanzhu.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shanzhu.hospital.entity.po.Drug;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 药物 Mapper接口
 */
public interface DrugMapper extends BaseMapper<Drug> {
    /**
     * 根据药物名称查询药物
     */
    @Select("SELECT * FROM drug WHERE dr_name = #{drName}")
    Drug selectByName(@Param("drName") String drName);
}