package com.riskgis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskgis.model.RiskAssessment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface RiskAssessmentMapper extends BaseMapper<RiskAssessment> {

    @Select("SELECT id, region_id, risk_type, risk_score, risk_level, assessment_date, factors, ST_AsText(geometry) as geometry, created_at FROM risk_assessment WHERE risk_type = #{riskType}")
    List<RiskAssessment> selectByRiskType(@Param("riskType") String riskType);

    @Select("SELECT id, region_id, risk_type, risk_score, risk_level, assessment_date, factors, ST_AsText(geometry) as geometry, created_at FROM risk_assessment WHERE risk_level = #{riskLevel}")
    List<RiskAssessment> selectByRiskLevel(@Param("riskLevel") String riskLevel);

    @Select("SELECT id, region_id, risk_type, risk_score, risk_level, assessment_date, factors, ST_AsText(geometry) as geometry, created_at FROM risk_assessment WHERE geometry && ST_MakeEnvelope(#{minLng}, #{minLat}, #{maxLng}, #{maxLat}, 4326)")
    List<RiskAssessment> selectByBounds(@Param("minLng") double minLng, @Param("minLat") double minLat,
                                         @Param("maxLng") double maxLng, @Param("maxLat") double maxLat);

    @Select("SELECT risk_level, COUNT(*) as count FROM risk_assessment GROUP BY risk_level")
    List<Map<String, Object>> countByRiskLevel();
}
