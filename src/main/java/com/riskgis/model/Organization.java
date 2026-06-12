package com.riskgis.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("organization")
public class Organization {
    @TableId(type = IdType.INPUT)
    private String orgCode;

    private String orgName;

    private String parentCode;

    private Integer level;

    private Boolean enabled;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
