package com.riskgis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskgis.model.InsurancePolicy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InsurancePolicyMapper extends BaseMapper<InsurancePolicy> {

    List<InsurancePolicy> selectByBuffer(
        @Param("lng") double lng,
        @Param("lat") double lat,
        @Param("radiusMeters") double radiusMeters,
        @Param("endDate") String endDate,
        @Param("categoryCodes") List<String> categoryCodes,
        @Param("typeCodes") List<String> typeCodes
    );

    List<InsurancePolicy> selectByGeometry(
        @Param("wkt") String wkt,
        @Param("categoryCodes") List<String> categoryCodes,
        @Param("typeCodes") List<String> typeCodes
    );

    List<InsurancePolicy> selectByTyphoonBuffer(
        @Param("wkt") String wkt,
        @Param("radiusMeters") double radiusMeters,
        @Param("endDate") String endDate,
        @Param("orgCodes") List<String> orgCodes,
        @Param("categoryCodes") List<String> categoryCodes,
        @Param("typeCodes") List<String> typeCodes
    );
}
