package com.riskgis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskgis.mapper.OrganizationMapper;
import com.riskgis.model.Organization;
import com.riskgis.service.OrganizationService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationMapper organizationMapper;

    public OrganizationServiceImpl(OrganizationMapper organizationMapper) {
        this.organizationMapper = organizationMapper;
    }

    @Override
    public List<Map<String, Object>> getTree() {
        List<Organization> all = organizationMapper.selectList(
                new LambdaQueryWrapper<Organization>()
                        .eq(Organization::getEnabled, true)
                        .orderByAsc(Organization::getOrgCode)
        );

        // Group by parent code
        Map<String, List<Organization>> byParent = all.stream()
                .filter(o -> o.getParentCode() != null)
                .collect(Collectors.groupingBy(Organization::getParentCode));

        // Find root (总公司)
        Organization root = all.stream()
                .filter(o -> o.getParentCode() == null)
                .findFirst()
                .orElse(null);

        if (root == null) {
            return Collections.emptyList();
        }

        // Build tree recursively
        List<Map<String, Object>> tree = new ArrayList<>();
        tree.add(buildNode(root, byParent));
        return tree;
    }

    private Map<String, Object> buildNode(Organization org, Map<String, List<Organization>> byParent) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("value", org.getOrgCode());
        node.put("label", org.getOrgName());

        List<Organization> children = byParent.getOrDefault(org.getOrgCode(), Collections.emptyList());
        if (!children.isEmpty()) {
            node.put("children", children.stream()
                    .map(child -> buildNode(child, byParent))
                    .collect(Collectors.toList()));
        }

        return node;
    }
}
