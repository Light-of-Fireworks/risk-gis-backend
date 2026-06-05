package com.riskgis.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("warning")
public class Warning {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String warningType;

    private String level;

    private String title;

    private String content;

    private String location;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
