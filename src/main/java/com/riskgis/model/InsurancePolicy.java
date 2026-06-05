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
@TableName("insurance_policy")
public class InsurancePolicy {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String policyNo;

    private String policyHolder;

    private String insuredName;

    private String categoryCode;

    private String typeCode;

    private Integer targetNo;

    private BigDecimal coverageAmount;

    private BigDecimal premium;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    private String address;

    private String location;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
