package com.riskgis.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("typhoon_point")
public class TyphoonPoint {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String tfid;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime pointTime;

    private String lat;

    private String lng;

    private String strong;

    private String power;

    private String speed;

    private String pressure;

    private String moveDirection;

    private String moveSpeed;

    private String radius7;

    private String radius10;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
