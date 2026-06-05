package com.riskgis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskgis.model.EarthquakeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface EarthquakeRecordMapper extends BaseMapper<EarthquakeRecord> {

    @Select("SELECT * FROM earthquake_record WHERE occur_time >= #{startTime} ORDER BY occur_time DESC")
    List<EarthquakeRecord> selectByTimeRange(@Param("startTime") LocalDateTime startTime);

    @Select("<script>" +
            "SELECT * FROM earthquake_record WHERE occur_time >= #{startTime}" +
            "<if test='endTime != null'> AND occur_time &lt;= #{endTime}</if>" +
            "<if test='minMagnitude != null'> AND magnitude &gt;= #{minMagnitude}</if>" +
            "<if test='maxMagnitude != null'> AND magnitude &lt;= #{maxMagnitude}</if>" +
            "<if test='minDepth != null'> AND depth &gt;= #{minDepth}</if>" +
            "<if test='maxDepth != null'> AND depth &lt;= #{maxDepth}</if>" +
            " ORDER BY occur_time DESC" +
            "</script>")
    List<EarthquakeRecord> selectByFilters(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("minMagnitude") BigDecimal minMagnitude,
            @Param("maxMagnitude") BigDecimal maxMagnitude,
            @Param("minDepth") BigDecimal minDepth,
            @Param("maxDepth") BigDecimal maxDepth);
}
