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
@TableName("disaster_record")
public class DisasterRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String disasterType;

    private String location;

    private String severity;

    private LocalDateTime occurrenceDate;

    private BigDecimal damageAmount;

    private String affectedArea;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
