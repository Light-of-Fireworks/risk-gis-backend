package com.riskgis.service.impl;

import com.riskgis.dto.request.InsurancePolicyQueryRequest;
import com.riskgis.dto.request.InsurancePolicyRegionQueryRequest;
import com.riskgis.dto.response.InsurancePolicyGroupVO;
import com.riskgis.dto.response.InsurancePolicyVO;
import com.riskgis.mapper.InsuranceCategoryMapper;
import com.riskgis.mapper.InsurancePolicyMapper;
import com.riskgis.mapper.InsuranceTypeMapper;
import com.riskgis.model.InsuranceCategory;
import com.riskgis.model.InsurancePolicy;
import com.riskgis.model.InsuranceType;
import com.riskgis.service.InsurancePolicyService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InsurancePolicyServiceImpl implements InsurancePolicyService {

    private final InsurancePolicyMapper policyMapper;
    private final InsuranceCategoryMapper categoryMapper;
    private final InsuranceTypeMapper typeMapper;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public InsurancePolicyServiceImpl(InsurancePolicyMapper policyMapper,
                                       InsuranceCategoryMapper categoryMapper,
                                       InsuranceTypeMapper typeMapper) {
        this.policyMapper = policyMapper;
        this.categoryMapper = categoryMapper;
        this.typeMapper = typeMapper;
    }

    @Override
    public List<InsurancePolicyGroupVO> queryByBuffer(InsurancePolicyQueryRequest request) {
        double radiusMeters = request.getRadius() * 1000.0;

        List<InsurancePolicy> policies = policyMapper.selectByBuffer(
            request.getLng(),
            request.getLat(),
            radiusMeters,
            request.getCategoryCodes(),
            request.getTypeCodes()
        );

        if (policies.isEmpty()) {
            return Collections.emptyList();
        }

        // 加载险类和险种名称映射
        Map<String, String> categoryNameMap = loadCategoryNameMap();
        Map<String, String> typeNameMap = loadTypeNameMap();

        // 按坐标分组（保留6位小数精度）
        Map<String, List<InsurancePolicy>> groupMap = new LinkedHashMap<>();
        for (InsurancePolicy p : policies) {
            String key = groupKey(p.getLocation());
            groupMap.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
        }

        // 转换为分组VO
        List<InsurancePolicyGroupVO> result = new ArrayList<>();
        for (Map.Entry<String, List<InsurancePolicy>> entry : groupMap.entrySet()) {
            List<InsurancePolicy> group = entry.getValue();
            double[] coords = parsePoint(group.get(0).getLocation());

            List<InsurancePolicyVO> voList = group.stream().map(p -> {
                InsurancePolicyVO vo = new InsurancePolicyVO();
                vo.setId(p.getId());
                vo.setPolicyNo(p.getPolicyNo());
                vo.setPolicyHolder(p.getPolicyHolder());
                vo.setInsuredName(p.getInsuredName());
                vo.setCategoryName(categoryNameMap.getOrDefault(p.getCategoryCode(), p.getCategoryCode()));
                vo.setTypeName(typeNameMap.getOrDefault(p.getTypeCode(), p.getTypeCode()));
                vo.setTargetNo(p.getTargetNo());
                vo.setCoverageAmount(p.getCoverageAmount());
                vo.setPremium(p.getPremium());
                vo.setStartDate(p.getStartDate() != null ? p.getStartDate().format(DATE_FMT) : null);
                vo.setEndDate(p.getEndDate() != null ? p.getEndDate().format(DATE_FMT) : null);
                vo.setStatus(p.getStatus());
                vo.setAddress(p.getAddress());
                return vo;
            }).collect(Collectors.toList());

            InsurancePolicyGroupVO groupVO = new InsurancePolicyGroupVO();
            groupVO.setLng(coords[0]);
            groupVO.setLat(coords[1]);
            groupVO.setCount(voList.size());
            groupVO.setPolicies(voList);
            result.add(groupVO);
        }

        return result;
    }

    @Override
    public List<InsurancePolicyGroupVO> queryByRegion(InsurancePolicyRegionQueryRequest request) {
        List<InsurancePolicy> policies = policyMapper.selectByGeometry(
            request.getGeometry(),
            request.getCategoryCodes(),
            request.getTypeCodes()
        );

        if (policies.isEmpty()) {
            return Collections.emptyList();
        }

        // 加载险类和险种名称映射
        Map<String, String> categoryNameMap = loadCategoryNameMap();
        Map<String, String> typeNameMap = loadTypeNameMap();

        // 按坐标分组（保留6位小数精度）
        Map<String, List<InsurancePolicy>> groupMap = new LinkedHashMap<>();
        for (InsurancePolicy p : policies) {
            String key = groupKey(p.getLocation());
            groupMap.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
        }

        // 转换为分组VO
        List<InsurancePolicyGroupVO> result = new ArrayList<>();
        for (Map.Entry<String, List<InsurancePolicy>> entry : groupMap.entrySet()) {
            List<InsurancePolicy> group = entry.getValue();
            double[] coords = parsePoint(group.get(0).getLocation());

            List<InsurancePolicyVO> voList = group.stream().map(p -> {
                InsurancePolicyVO vo = new InsurancePolicyVO();
                vo.setId(p.getId());
                vo.setPolicyNo(p.getPolicyNo());
                vo.setPolicyHolder(p.getPolicyHolder());
                vo.setInsuredName(p.getInsuredName());
                vo.setCategoryName(categoryNameMap.getOrDefault(p.getCategoryCode(), p.getCategoryCode()));
                vo.setTypeName(typeNameMap.getOrDefault(p.getTypeCode(), p.getTypeCode()));
                vo.setTargetNo(p.getTargetNo());
                vo.setCoverageAmount(p.getCoverageAmount());
                vo.setPremium(p.getPremium());
                vo.setStartDate(p.getStartDate() != null ? p.getStartDate().format(DATE_FMT) : null);
                vo.setEndDate(p.getEndDate() != null ? p.getEndDate().format(DATE_FMT) : null);
                vo.setStatus(p.getStatus());
                vo.setAddress(p.getAddress());
                return vo;
            }).collect(Collectors.toList());

            InsurancePolicyGroupVO groupVO = new InsurancePolicyGroupVO();
            groupVO.setLng(coords[0]);
            groupVO.setLat(coords[1]);
            groupVO.setCount(voList.size());
            groupVO.setPolicies(voList);
            result.add(groupVO);
        }

        return result;
    }

    private Map<String, String> loadCategoryNameMap() {
        List<InsuranceCategory> categories = categoryMapper.selectList(null);
        return categories.stream().collect(Collectors.toMap(
            InsuranceCategory::getCategoryCode,
            InsuranceCategory::getCategoryName,
            (a, b) -> a
        ));
    }

    private Map<String, String> loadTypeNameMap() {
        List<InsuranceType> types = typeMapper.selectList(null);
        return types.stream().collect(Collectors.toMap(
            InsuranceType::getTypeCode,
            InsuranceType::getTypeName,
            (a, b) -> a
        ));
    }

    /**
     * 生成分组key，将坐标保留6位小数
     */
    private String groupKey(String wktPoint) {
        double[] coords = parsePoint(wktPoint);
        return String.format("%.6f,%.6f", coords[0], coords[1]);
    }

    /**
     * 解析 WKT POINT 格式为 [lng, lat]
     */
    private double[] parsePoint(String wkt) {
        // POINT(lng lat)
        String coords = wkt.replace("POINT(", "").replace(")", "").trim();
        String[] parts = coords.split("\\s+");
        return new double[]{Double.parseDouble(parts[0]), Double.parseDouble(parts[1])};
    }
}
