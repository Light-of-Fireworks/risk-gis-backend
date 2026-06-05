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
@TableName("risk_factor_config")
public class RiskFactorConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String disasterType;
    private String factorName;
    private BigDecimal factorWeight;
    private String factorType;
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
