package com.riskgis.service;

import java.util.List;
import java.util.Map;

public interface InsuranceService {
    List<Map<String, Object>> getAllCategories();
    List<Map<String, Object>> getTypesByCategory(String categoryCode);
    List<Map<String, Object>> getTree();
}
