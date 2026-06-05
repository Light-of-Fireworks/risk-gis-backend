package com.riskgis.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("insurance_target")
public class InsuranceTarget {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String targetName;

    private String targetType;

    private String location;

    private BigDecimal riskScore;

    private BigDecimal premiumRate;

    private BigDecimal coverageAmount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
