package com.riskgis.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("risk_assessment")
public class RiskAssessment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long regionId;

    private String riskType;

    private BigDecimal riskScore;

    private String riskLevel;

    private LocalDate assessmentDate;

    private String factors;

    private String geometry;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
