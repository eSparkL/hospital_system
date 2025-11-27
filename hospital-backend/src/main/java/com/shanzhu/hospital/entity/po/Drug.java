package com.shanzhu.hospital.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 药物实体类
 */
@Data
@TableName("drug")
public class Drug {

    /**
     * 药物ID
     */
    @TableId(value = "dr_id")
    @JsonProperty("drId")
    private Integer drId;

    /**
     * 药物名称
     */
    @JsonProperty("drName")
    private String drName;

    /**
     * 药物价格
     */
    @JsonProperty("drPrice")
    private BigDecimal drPrice;

    /**
     * 药物数量
     */
    @JsonProperty("drNumber")
    private Integer drNumber;

    /**
     * 出版商/生产商
     */
    @JsonProperty("drPublisher")
    private String drPublisher;

    /**
     * 单位
     */
    @JsonProperty("drUnit")
    private String drUnit;
}