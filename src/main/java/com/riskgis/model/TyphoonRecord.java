package com.riskgis.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("typhoon_record")
public class TyphoonRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String tfid;

    private String name;

    private String enName;

    private String strong;

    private String power;

    private String speed;

    private String pressure;

    private BigDecimal lat;

    private BigDecimal lng;

    private String moveDirection;

    private String moveSpeed;

    private String radius7;

    private String radius10;

    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
