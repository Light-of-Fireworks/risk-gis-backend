package com.riskgis.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaAssessRequest {
    @NotBlank(message = "几何图形不能为空")
    private String geometry;

    private List<String> disasterTypes;
}
