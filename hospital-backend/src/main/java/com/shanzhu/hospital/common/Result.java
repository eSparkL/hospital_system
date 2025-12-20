package com.shanzhu.hospital.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private boolean success; // 是否成功
    private String message;  // 提示信息
    private T data;          // 返回数据（可选）
}
