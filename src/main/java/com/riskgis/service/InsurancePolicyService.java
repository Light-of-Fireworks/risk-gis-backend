package com.riskgis.service;

import com.riskgis.dto.request.InsurancePolicyQueryRequest;
import com.riskgis.dto.request.InsurancePolicyRegionQueryRequest;
import com.riskgis.dto.response.InsurancePolicyGroupVO;

import java.util.List;

public interface InsurancePolicyService {
    List<InsurancePolicyGroupVO> queryByBuffer(InsurancePolicyQueryRequest request);
    List<InsurancePolicyGroupVO> queryByRegion(InsurancePolicyRegionQueryRequest request);
}
