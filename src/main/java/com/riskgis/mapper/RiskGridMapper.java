package com.riskgis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskgis.model.RiskGrid;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface RiskGridMapper extends BaseMapper<RiskGrid> {

    List<RiskGrid> selectByPoint(
        @Param("lng") double lng,
        @Param("lat") double lat,
        @Param("disasterTypes") List<String> disasterTypes
    );

    List<Map<String, Object>> selectByArea(
        @Param("wkt") String wkt,
        @Param("disasterType") String disasterType
    );

    List<RiskGrid> selectByBounds(
        @Param("minLng") double minLng,
        @Param("minLat") double minLat,
        @Param("maxLng") double maxLng,
        @Param("maxLat") double maxLat,
        @Param("disasterTypes") List<String> disasterTypes
    );
}
