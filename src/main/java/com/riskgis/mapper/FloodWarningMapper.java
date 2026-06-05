package com.riskgis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskgis.model.FloodWarning;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FloodWarningMapper extends BaseMapper<FloodWarning> {

    @Select("SELECT * FROM flood_warning WHERE publish_time >= #{startTime} ORDER BY publish_time DESC")
    List<FloodWarning> selectByTimeRange(@Param("startTime") LocalDateTime startTime);

    @Select("SELECT * FROM flood_warning WHERE wr_level = #{level} ORDER BY publish_time DESC")
    List<FloodWarning> selectByLevel(@Param("level") String level);

    @Select("SELECT * FROM flood_warning WHERE wr_type = #{wrType} ORDER BY publish_time DESC")
    List<FloodWarning> selectByWrType(@Param("wrType") String wrType);

    @Select("SELECT * FROM flood_warning WHERE publish_time >= #{startTime} AND publish_time <= #{endTime} ORDER BY publish_time DESC")
    List<FloodWarning> selectByDateRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
