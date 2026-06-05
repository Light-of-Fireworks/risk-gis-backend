package com.riskgis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnderwritingRequest {
    @NotBlank(message = "标的名称不能为空")
    private String name;

    @NotBlank(message = "标的类型不能为空")
    private String type;

    @NotBlank(message = "地址不能为空")
    private String address;

    @NotNull(message = "保额不能为空")
    @Positive(message = "保额必须大于0")
    private BigDecimal amount;

    private String location;
}
