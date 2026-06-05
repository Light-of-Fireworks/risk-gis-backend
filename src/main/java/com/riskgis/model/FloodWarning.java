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
@TableName("flood_warning")
public class FloodWarning {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long wrInfoId;

    private String wrIcon;

    private String wrTitle;

    private String wrDetail;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String wrType;

    private String wrLevel;

    private String influenceArea;

    private String influenceAreaCd;

    private String unitName;

    private String detailUrl;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
