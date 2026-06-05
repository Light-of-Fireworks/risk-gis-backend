package com.riskgis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskgis.model.TyphoonRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TyphoonMapper extends BaseMapper<TyphoonRecord> {

    @Select("SELECT * FROM typhoon_record ORDER BY data_time DESC")
    List<TyphoonRecord> selectAll();

    @Select("SELECT * FROM typhoon_record WHERE tfid = #{tfid}")
    TyphoonRecord selectByTfid(@Param("tfid") String tfid);

    @Select("SELECT * FROM typhoon_record WHERE is_active = true ORDER BY data_time DESC")
    List<TyphoonRecord> selectActive();

    @Select("SELECT DISTINCT EXTRACT(YEAR FROM data_time)::int AS year FROM typhoon_record WHERE data_time IS NOT NULL ORDER BY year DESC")
    List<Integer> selectYears();

    @Select("SELECT * FROM typhoon_record WHERE EXTRACT(YEAR FROM data_time) = #{year} ORDER BY data_time DESC")
    List<TyphoonRecord> selectByYear(@Param("year") int year);
}
