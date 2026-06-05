package com.riskgis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskgis.model.Warning;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WarningMapper extends BaseMapper<Warning> {

    @Select("SELECT id, warning_type, level, title, content, ST_AsText(location) as location, start_time, end_time, status, created_at FROM warning WHERE warning_type = #{warningType}")
    List<Warning> selectByWarningType(@Param("warningType") String warningType);

    @Select("SELECT id, warning_type, level, title, content, ST_AsText(location) as location, start_time, end_time, status, created_at FROM warning WHERE level = #{level}")
    List<Warning> selectByLevel(@Param("level") String level);

    @Select("SELECT id, warning_type, level, title, content, ST_AsText(location) as location, start_time, end_time, status, created_at FROM warning WHERE status = #{status}")
    List<Warning> selectByStatus(@Param("status") String status);

    @Select("SELECT id, warning_type, level, title, content, ST_AsText(location) as location, start_time, end_time, status, created_at FROM warning WHERE location && ST_MakeEnvelope(#{minLng}, #{minLat}, #{maxLng}, #{maxLat}, 4326)")
    List<Warning> selectByBounds(@Param("minLng") double minLng, @Param("minLat") double minLat,
                                  @Param("maxLng") double maxLng, @Param("maxLat") double maxLat);
}
