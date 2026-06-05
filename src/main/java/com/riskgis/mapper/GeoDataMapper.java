package com.riskgis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskgis.model.GeoData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GeoDataMapper extends BaseMapper<GeoData> {

    @Select("SELECT id, name, type, ST_AsText(geometry) as geometry, properties, created_at, updated_at FROM geo_data WHERE type = #{type}")
    List<GeoData> selectByType(@Param("type") String type);

    @Select("SELECT id, name, type, ST_AsText(geometry) as geometry, properties, created_at, updated_at FROM geo_data WHERE geometry && ST_MakeEnvelope(#{minLng}, #{minLat}, #{maxLng}, #{maxLat}, 4326)")
    List<GeoData> selectByBounds(@Param("minLng") double minLng, @Param("minLat") double minLat,
                                  @Param("maxLng") double maxLng, @Param("maxLat") double maxLat);

    @Select("SELECT id, name, type, ST_AsText(geometry) as geometry, properties, created_at, updated_at FROM geo_data WHERE ST_Intersects(geometry, ST_GeomFromText(#{wkt}, 4326))")
    List<GeoData> selectByGeometry(@Param("wkt") String wkt);

    @Select("SELECT id, name, type, ST_AsText(geometry) as geometry, properties, created_at, updated_at FROM geo_data WHERE properties->>'adcode' = #{regionCode} LIMIT 1")
    List<GeoData> selectByRegionCode(@Param("regionCode") String regionCode);
}
