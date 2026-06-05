package com.riskgis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskgis.model.DisasterRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface DisasterRecordMapper extends BaseMapper<DisasterRecord> {

    @Select("SELECT id, disaster_type, ST_AsText(location) as location, severity, occurrence_date, damage_amount, ST_AsText(affected_area) as affected_area, description, created_at FROM disaster_record WHERE disaster_type = #{disasterType}")
    List<DisasterRecord> selectByDisasterType(@Param("disasterType") String disasterType);

    @Select("SELECT id, disaster_type, ST_AsText(location) as location, severity, occurrence_date, damage_amount, ST_AsText(affected_area) as affected_area, description, created_at FROM disaster_record WHERE occurrence_date BETWEEN #{startDate} AND #{endDate}")
    List<DisasterRecord> selectByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Select("SELECT id, disaster_type, ST_AsText(location) as location, severity, occurrence_date, damage_amount, ST_AsText(affected_area) as affected_area, description, created_at FROM disaster_record WHERE location && ST_MakeEnvelope(#{minLng}, #{minLat}, #{maxLng}, #{maxLat}, 4326)")
    List<DisasterRecord> selectByBounds(@Param("minLng") double minLng, @Param("minLat") double minLat,
                                         @Param("maxLng") double maxLng, @Param("maxLat") double maxLat);

    @Select("SELECT disaster_type, COUNT(*) as count FROM disaster_record GROUP BY disaster_type")
    List<Map<String, Object>> countByDisasterType();
}
