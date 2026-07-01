package com.riskgis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskgis.client.ShowApiClient;
import com.riskgis.dto.typhoon.ShowApiDetailResponse;
import com.riskgis.dto.typhoon.ShowApiListResponse;
import com.riskgis.dto.typhoon.TyphoonPointDTO;
import com.riskgis.mapper.TyphoonMapper;
import com.riskgis.mapper.TyphoonPointMapper;
import com.riskgis.model.TyphoonPoint;
import com.riskgis.model.TyphoonRecord;
import com.riskgis.service.TyphoonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TyphoonServiceImpl implements TyphoonService {

    private static final Logger log = LoggerFactory.getLogger(TyphoonServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TyphoonMapper typhoonMapper;
    private final TyphoonPointMapper typhoonPointMapper;
    private final ShowApiClient showApiClient;

    @Value("${showapi.app-key}")
    private String appKey;

    public TyphoonServiceImpl(TyphoonMapper typhoonMapper, TyphoonPointMapper typhoonPointMapper, ShowApiClient showApiClient) {
        this.typhoonMapper = typhoonMapper;
        this.typhoonPointMapper = typhoonPointMapper;
        this.showApiClient = showApiClient;
    }

    @Override
    public void syncTyphoonData() {
        try {
            String year = String.valueOf(Year.now().getValue());
            ShowApiListResponse response = showApiClient.getTyphoonList(appKey, year);

            if (response == null || response.getShowapiResBody() == null) {
                log.warn("台风列表接口返回为空");
                return;
            }

            List<ShowApiListResponse.TyphoonItem> typhoonList = response.getShowapiResBody().getTyphoonList();
            if (typhoonList == null || typhoonList.isEmpty()) {
                log.info("当前无台风数据");
                return;
            }

            int newCount = 0;
            int updatedCount = 0;
            for (ShowApiListResponse.TyphoonItem item : typhoonList) {
                try {
                    String tfid = item.getTfid();
                    String name = item.getName();
                    String enName = item.getEn();
                    TyphoonRecord existing = typhoonMapper.selectByTfid(tfid);

                    if (existing == null) {
                        TyphoonRecord record = new TyphoonRecord();
                        record.setTfid(tfid);
                        record.setName(name);
                        record.setEnName(enName);
                        record.setIsActive(true);
                        boolean hasDetail = fetchAndSaveDetail(tfid, record);
                        if (hasDetail) {
                            newCount++;
                        } else {
                            log.warn("台风 {} 详情接口无数据，跳过入库", tfid);
                        }
                    } else if (Boolean.TRUE.equals(existing.getIsActive())) {
                        fetchAndSaveDetail(tfid, existing);
                        updatedCount++;
                    }
                } catch (Exception e) {
                    log.warn("处理台风数据失败 tfid={}: {}", item.getTfid(), e.getMessage());
                }
            }

            log.info("台风同步完成: 新增 {}, 更新 {}", newCount, updatedCount);
        } catch (Exception e) {
            log.error("台风数据同步失败: {}", e.getMessage());
        }
    }

    private boolean fetchAndSaveDetail(String tfid, TyphoonRecord record) {
        boolean isNew = (record.getId() == null);
        try {
            LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("tfid", tfid);

            ShowApiDetailResponse response = showApiClient.getTyphoonDetail(appKey, params);

            if (response == null || response.getShowapiResBody() == null) {
                log.warn("台风详情接口返回为空, tfid={}", tfid);
                return false;
            }

            ShowApiDetailResponse.TyphoonDetail detail = response.getShowapiResBody().getDetail();
            if (detail == null) {
                log.warn("台风详情接口 obj 为 null, tfid={}", tfid);
                return false;
            }

            record.setName(detail.getName());
            record.setEnName(detail.getEnname());
            record.setIsActive("1".equals(detail.getIsactive()));

            List<TyphoonPointDTO> points = detail.getPoints();
            if (points != null && !points.isEmpty()) {
                TyphoonPointDTO lastPoint = points.get(points.size() - 1);
                record.setStrong(lastPoint.getStrong());
                record.setPower(lastPoint.getPower());
                record.setSpeed(lastPoint.getSpeed());
                record.setPressure(lastPoint.getPressure());
                record.setLat(new BigDecimal(lastPoint.getLat()));
                record.setLng(new BigDecimal(lastPoint.getLng()));
                record.setMoveDirection(lastPoint.getMovedirection());
                record.setMoveSpeed(lastPoint.getMovespeed());
                record.setRadius7(lastPoint.getRadius7());
                record.setRadius10(lastPoint.getRadius10());
                if (lastPoint.getTime() != null) {
                    try {
                        record.setDataTime(LocalDateTime.parse(lastPoint.getTime(), FORMATTER));
                    } catch (Exception ignored) {
                    }
                }
            } else {
                return false;
            }

            if (isNew) {
                typhoonMapper.insert(record);
            } else {
                typhoonMapper.updateById(record);
            }

            typhoonPointMapper.deleteByTfid(tfid);
            for (TyphoonPointDTO pd : points) {
                TyphoonPoint tp = new TyphoonPoint();
                tp.setTfid(tfid);
                tp.setLat(pd.getLat());
                tp.setLng(pd.getLng());
                tp.setStrong(pd.getStrong());
                tp.setPower(pd.getPower());
                tp.setSpeed(pd.getSpeed());
                tp.setPressure(pd.getPressure());
                tp.setMoveDirection(pd.getMovedirection());
                tp.setMoveSpeed(pd.getMovespeed());
                tp.setRadius7(pd.getRadius7());
                tp.setRadius10(pd.getRadius10());
                if (pd.getTime() != null) {
                    try {
                        tp.setPointTime(LocalDateTime.parse(pd.getTime(), FORMATTER));
                    } catch (Exception ignored) {
                    }
                }
                typhoonPointMapper.insert(tp);
            }
            log.debug("台风 {} 数据已保存，轨迹点 {} 个", tfid, points.size());
            return true;
        } catch (Exception e) {
            log.error("获取台风详情失败 tfid={}: {}", tfid, e.getMessage());
            return false;
        }
    }

    private String getStringOrDefault(ShowApiDetailResponse.TyphoonDetail map, String key, String defaultValue) {
        // kept for compatibility - values accessed via getter methods on typed DTO now
        return defaultValue;
    }

    @Override
    public List<TyphoonRecord> getAllTyphoons() {
        return typhoonMapper.selectAll();
    }

    @Override
    public List<TyphoonPoint> getTyphoonPoints(String tfid) {
        return typhoonPointMapper.selectByTfid(tfid);
    }

    @Override
    public List<TyphoonRecord> getActiveTyphoons() {
        return typhoonMapper.selectActive();
    }

    @Override
    public List<Integer> getTyphoonYears() {
        return typhoonMapper.selectYears();
    }

    @Override
    public List<TyphoonRecord> getTyphoonsByYear(int year) {
        return typhoonMapper.selectByYear(year);
    }
}
