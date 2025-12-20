package com.shanzhu.hospital.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_drug")
public class OrderDrug {

    private Integer id;
    private Integer oId;
    private Integer drId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private LocalDateTime createTime;
}
