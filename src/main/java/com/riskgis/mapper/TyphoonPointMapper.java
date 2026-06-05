package com.riskgis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskgis.model.TyphoonPoint;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TyphoonPointMapper extends BaseMapper<TyphoonPoint> {

    @Select("SELECT * FROM typhoon_point WHERE tfid = #{tfid} ORDER BY point_time ASC")
    List<TyphoonPoint> selectByTfid(@Param("tfid") String tfid);

    @Delete("DELETE FROM typhoon_point WHERE tfid = #{tfid}")
    int deleteByTfid(@Param("tfid") String tfid);
}
