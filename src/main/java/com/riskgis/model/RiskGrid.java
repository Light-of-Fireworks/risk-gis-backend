package com.riskgis.model;

import com.baomidou.mybatisplus.annotation.*;
import com.riskgis.config.GeometryTypeHandler;
import com.riskgis.config.JsonbTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "risk_grid", autoResultMap = true)
public class RiskGrid {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String disasterType;
    private BigDecimal riskScore;
    private String riskLevel;
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String factors;
    @TableField(typeHandler = GeometryTypeHandler.class)
    private String geometry;
    private Integer gridSize;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
