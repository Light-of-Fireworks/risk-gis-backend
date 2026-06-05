package com.riskgis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskgis.model.InsuranceTarget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InsuranceTargetMapper extends BaseMapper<InsuranceTarget> {

    @Select("SELECT id, target_name, target_type, ST_AsText(location) as location, risk_score, premium_rate, coverage_amount, created_at FROM insurance_target WHERE target_type = #{targetType}")
    List<InsuranceTarget> selectByTargetType(@Param("targetType") String targetType);

    @Select("SELECT id, target_name, target_type, ST_AsText(location) as location, risk_score, premium_rate, coverage_amount, created_at FROM insurance_target WHERE location && ST_MakeEnvelope(#{minLng}, #{minLat}, #{maxLng}, #{maxLat}, 4326)")
    List<InsuranceTarget> selectByBounds(@Param("minLng") double minLng, @Param("minLat") double minLat,
                                          @Param("maxLng") double maxLng, @Param("maxLat") double maxLat);
}
