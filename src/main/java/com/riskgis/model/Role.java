package com.riskgis.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("roles")
public class Role {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;
}
