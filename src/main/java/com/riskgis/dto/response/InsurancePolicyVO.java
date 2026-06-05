package com.riskgis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePolicyVO {
    private Long id;
    private String policyNo;
    private String policyHolder;
    private String insuredName;
    private String categoryName;
    private String typeName;
    private Integer targetNo;
    private BigDecimal coverageAmount;
    private BigDecimal premium;
    private String startDate;
    private String endDate;
    private String status;
    private String address;
}
