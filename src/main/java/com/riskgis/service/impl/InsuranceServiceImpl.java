package com.riskgis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskgis.mapper.InsuranceCategoryMapper;
import com.riskgis.mapper.InsuranceTypeMapper;
import com.riskgis.model.InsuranceCategory;
import com.riskgis.model.InsuranceType;
import com.riskgis.service.InsuranceService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InsuranceServiceImpl implements InsuranceService {

    private final InsuranceCategoryMapper categoryMapper;
    private final InsuranceTypeMapper typeMapper;

    public InsuranceServiceImpl(InsuranceCategoryMapper categoryMapper, InsuranceTypeMapper typeMapper) {
        this.categoryMapper = categoryMapper;
        this.typeMapper = typeMapper;
    }

    @Override
    public List<Map<String, Object>> getAllCategories() {
        List<InsuranceCategory> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<InsuranceCategory>()
                        .eq(InsuranceCategory::getEnabled, true)
                        .orderByAsc(InsuranceCategory::getCategoryCode)
        );
        return categories.stream().map(c -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("value", c.getCategoryCode());
            map.put("label", c.getCategoryName());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getTypesByCategory(String categoryCode) {
        List<InsuranceType> types = typeMapper.selectList(
                new LambdaQueryWrapper<InsuranceType>()
                        .eq(InsuranceType::getCategoryCode, categoryCode)
                        .eq(InsuranceType::getEnabled, true)
                        .orderByAsc(InsuranceType::getTypeCode)
        );
        return types.stream().map(t -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("value", t.getTypeCode());
            map.put("label", t.getTypeName());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getTree() {
        List<InsuranceCategory> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<InsuranceCategory>()
                        .eq(InsuranceCategory::getEnabled, true)
                        .orderByAsc(InsuranceCategory::getCategoryCode)
        );
        List<InsuranceType> types = typeMapper.selectList(
                new LambdaQueryWrapper<InsuranceType>()
                        .eq(InsuranceType::getEnabled, true)
                        .orderByAsc(InsuranceType::getTypeCode)
        );

        Map<String, List<InsuranceType>> typeMap = types.stream()
                .collect(Collectors.groupingBy(InsuranceType::getCategoryCode));

        return categories.stream().map(c -> {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("value", c.getCategoryCode());
            node.put("label", c.getCategoryName());
            List<InsuranceType> catTypes = typeMap.getOrDefault(c.getCategoryCode(), Collections.emptyList());
            node.put("children", catTypes.stream().map(t -> {
                Map<String, Object> child = new LinkedHashMap<>();
                child.put("value", t.getTypeCode());
                child.put("label", t.getTypeName());
                return child;
            }).collect(Collectors.toList()));
            return node;
        }).collect(Collectors.toList());
    }
}
