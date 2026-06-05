package com.riskgis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskgis.mapper.TyphoonMapper;
import com.riskgis.mapper.TyphoonPointMapper;
import com.riskgis.model.TyphoonPoint;
import com.riskgis.model.TyphoonRecord;
import com.riskgis.service.TyphoonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class TyphoonServiceImpl implements TyphoonService {

    private static final Logger log = LoggerFactory.getLogger(TyphoonServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TyphoonMapper typhoonMapper;
    private final TyphoonPointMapper typhoonPointMapper;

    @Value("${showapi.app-key}")
    private String appKey;

    public TyphoonServiceImpl(TyphoonMapper typhoonMapper, TyphoonPointMapper typhoonPointMapper) {
        this.typhoonMapper = typhoonMapper;
        this.typhoonPointMapper = typhoonPointMapper;
    }

    private RestTemplate createShowApiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN));
        restTemplate.getMessageConverters().add(0, converter);
        return restTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void syncTyphoonData() {
        try {
            RestTemplate restTemplate = createShowApiRestTemplate();
            // 使用 342-3 历史台风列表接口，传入当年年份
            String year = String.valueOf(Year.now().getValue());
            String url = "https://route.showapi.com/342-3?appKey=" + appKey + "&year=" + year;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey("showapi_res_body")) {
                log.warn("台风列表接口返回为空");
                return;
            }

            Map<String, Object> body = (Map<String, Object>) response.get("showapi_res_body");
            if (body == null || !body.containsKey("typhoon_list")) {
                log.warn("台风列表数据为空");
                return;
            }

            List<?> typhoonList = (List<?>) body.get("typhoon_list");
            if (typhoonList == null || typhoonList.isEmpty()) {
                log.info("当前无台风数据");
                return;
            }

            int newCount = 0;
            int updatedCount = 0;
            for (Object item : typhoonList) {
                Map<String, Object> data = (Map<String, Object>) item;
                try {
                    String tfid = (String) data.get("tfid");
                    String name = (String) data.get("name");
                    String enName = (String) data.get("en");
                    TyphoonRecord existing = typhoonMapper.selectByTfid(tfid);

                    if (existing == null) {
                        // 新台风：先调详情接口拿到完整数据，再入库
                        TyphoonRecord record = new TyphoonRecord();
                        record.setTfid(tfid);
                        record.setName(name);
                        record.setEnName(enName);
                        record.setIsActive(true);
                        boolean hasDetail = fetchAndSaveDetail(tfid, record);
                        if (hasDetail) {
                            // fetchAndSaveDetail 内部已完成 insert
                            newCount++;
                        } else {
                            log.warn("台风 {} 详情接口无数据，跳过入库", tfid);
                        }
                    } else if (Boolean.TRUE.equals(existing.getIsActive())) {
                        // 已有且仍活跃：调详情接口更新
                        fetchAndSaveDetail(tfid, existing);
                        updatedCount++;
                    }
                    // 已有且已停止，跳过不调详情接口
                } catch (Exception e) {
                    log.warn("处理台风数据失败 tfid={}: {}", ((Map<?, ?>) item).get("tfid"), e.getMessage());
                }
            }

            log.info("台风同步完成: 新增 {}, 更新 {}", newCount, updatedCount);
        } catch (Exception e) {
            log.error("台风数据同步失败: {}", e.getMessage());
        }
    }

    /**
     * 调用 342-2 详情接口，填充台风主表信息并保存轨迹点。
     *
     * @param tfid     台风ID
     * @param record   待填充的台风记录（isNew=true时为新记录，尚未入库；否则为已有记录）
     * @return true=成功获取到详情数据，false=接口无数据或调用失败
     */
    @SuppressWarnings("unchecked")
    private boolean fetchAndSaveDetail(String tfid, TyphoonRecord record) {
        boolean isNew = (record.getId() == null);
        try {
            RestTemplate restTemplate = createShowApiRestTemplate();
            String url = "https://route.showapi.com/342-2?appKey=" + appKey;
            log.info("请求台风详情: tfid={}", tfid);
            // 使用 POST + form-urlencoded 方式调用
            LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("tfid", tfid);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<LinkedMultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
            Map<String, Object> response = restTemplate.postForObject(url, requestEntity, Map.class);

            if (response == null) {
                log.warn("台风详情接口返回 null, tfid={}", tfid);
                return false;
            }
            if (!response.containsKey("showapi_res_body")) {
                log.warn("台风详情接口无 showapi_res_body, tfid={}, keys={}", tfid, response.keySet());
                return false;
            }

            Map<String, Object> body = (Map<String, Object>) response.get("showapi_res_body");
            if (body == null) {
                log.warn("台风详情接口 showapi_res_body 为 null, tfid={}", tfid);
                return false;
            }

            // 详情数据嵌套在 obj 中
            Map<String, Object> detail = (Map<String, Object>) body.get("obj");
            if (detail == null) {
                log.warn("台风详情接口 obj 为 null, tfid={}, body keys={}", tfid, body.keySet());
                return false;
            }

            // 填充主表信息
            record.setName(getStringOrDefault(detail, "name", record.getName()));
            record.setEnName(getStringOrDefault(detail, "enname", record.getEnName()));

            String isactive = (String) detail.get("isactive");
            record.setIsActive("1".equals(isactive));

            // 用最后一个轨迹点作为当前状态
            List<?> points = (List<?>) detail.get("points");
            if (points != null && !points.isEmpty()) {
                Map<String, Object> lastPoint = (Map<String, Object>) points.get(points.size() - 1);
                record.setStrong((String) lastPoint.get("strong"));
                record.setPower((String) lastPoint.get("power"));
                record.setSpeed((String) lastPoint.get("speed"));
                record.setPressure((String) lastPoint.get("pressure"));
                record.setLat(new BigDecimal((String) lastPoint.get("lat")));
                record.setLng(new BigDecimal((String) lastPoint.get("lng")));
                record.setMoveDirection((String) lastPoint.get("movedirection"));
                record.setMoveSpeed((String) lastPoint.get("movespeed"));
                record.setRadius7((String) lastPoint.get("radius7"));
                record.setRadius10((String) lastPoint.get("radius10"));
                String timeStr = (String) lastPoint.get("time");
                if (timeStr != null) {
                    try {
                        record.setDataTime(LocalDateTime.parse(timeStr, FORMATTER));
                    } catch (Exception ignored) {
                    }
                }
            } else {
                // 详情接口没有轨迹点数据，视为无效
                return false;
            }

            // 写入主表：新记录 insert，已有记录 update
            if (isNew) {
                typhoonMapper.insert(record);
            } else {
                typhoonMapper.updateById(record);
            }

            // 保存轨迹点（先删后插）
            typhoonPointMapper.deleteByTfid(tfid);
            for (Object p : points) {
                Map<String, Object> pd = (Map<String, Object>) p;
                TyphoonPoint tp = new TyphoonPoint();
                tp.setTfid(tfid);
                tp.setLat((String) pd.get("lat"));
                tp.setLng((String) pd.get("lng"));
                tp.setStrong((String) pd.get("strong"));
                tp.setPower((String) pd.get("power"));
                tp.setSpeed((String) pd.get("speed"));
                tp.setPressure((String) pd.get("pressure"));
                tp.setMoveDirection((String) pd.get("movedirection"));
                tp.setMoveSpeed((String) pd.get("movespeed"));
                tp.setRadius7((String) pd.get("radius7"));
                tp.setRadius10((String) pd.get("radius10"));
                String timeStr = (String) pd.get("time");
                if (timeStr != null) {
                    try {
                        tp.setPointTime(LocalDateTime.parse(timeStr, FORMATTER));
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

    private String getStringOrDefault(Map<String, Object> map, String key, String defaultValue) {
        Object val = map.get(key);
        return val != null ? (String) val : defaultValue;
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
