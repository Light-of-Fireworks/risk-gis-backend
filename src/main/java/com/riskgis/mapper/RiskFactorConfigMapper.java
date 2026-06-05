package com.riskgis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskgis.model.RiskFactorConfig;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface RiskFactorConfigMapper extends BaseMapper<RiskFactorConfig> {
    List<RiskFactorConfig> selectByDisasterType(String disasterType);
}
