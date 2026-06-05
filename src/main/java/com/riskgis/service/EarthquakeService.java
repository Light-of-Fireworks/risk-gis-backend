package com.riskgis.service;

import com.riskgis.model.EarthquakeRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface EarthquakeService {

    /**
     * 从 API 同步地震数据到数据库
     */
    void syncEarthquakeData();

    /**
     * 按时间范围查询地震数据
     *
     * @param range 时间范围：24h, 7d, 30d
     * @return 地震记录列表
     */
    List<EarthquakeRecord> getEarthquakeByTimeRange(String range);

    /**
     * 按条件筛选地震数据
     *
     * @param startTime    开始时间
     * @param endTime      结束时间（可选）
     * @param minMagnitude 最小震级（可选）
     * @param maxMagnitude 最大震级（可选）
     * @param minDepth     最小深度（可选）
     * @param maxDepth     最大深度（可选）
     * @return 地震记录列表
     */
    List<EarthquakeRecord> getEarthquakeByFilters(LocalDateTime startTime, LocalDateTime endTime, BigDecimal minMagnitude, BigDecimal maxMagnitude, BigDecimal minDepth, BigDecimal maxDepth);
}
