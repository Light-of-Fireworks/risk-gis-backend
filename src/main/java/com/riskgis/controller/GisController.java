package com.riskgis.controller;

import com.riskgis.client.AmapClient;
import com.riskgis.client.OpenTopoDataClient;
import com.riskgis.dto.amap.AmapDistrictResponse;
import com.riskgis.dto.amap.AmapInputTipsResponse;
import com.riskgis.dto.amap.AmapRegeoResponse;
import com.riskgis.dto.opentopodata.OpenTopoDataResponse;
import com.riskgis.dto.request.SpatialQueryRequest;
import com.riskgis.dto.response.ApiResponse;
import com.riskgis.model.EarthquakeRecord;
import com.riskgis.model.FloodWarning;
import com.riskgis.model.GeoData;
import com.riskgis.model.TyphoonPoint;
import com.riskgis.model.TyphoonRecord;
import com.riskgis.service.EarthquakeService;
import com.riskgis.service.FloodWarningService;
import com.riskgis.service.GisService;
import com.riskgis.service.TyphoonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GIS 管理控制器
 * <p>
 * 提供 GIS 空间数据相关的 API 接口，包括空间数据查询、空间分析、地理编码等。
 * </p>
 */
@RestController
@RequestMapping("/gis")
public class GisController {

    private final GisService gisService;
    private final EarthquakeService earthquakeService;
    private final FloodWarningService floodWarningService;
    private final TyphoonService typhoonService;
    private final AmapClient amapClient;
    private final OpenTopoDataClient openTopoDataClient;

    @Value("${amap.key}")
    private String amapKey;

    public GisController(GisService gisService, EarthquakeService earthquakeService, FloodWarningService floodWarningService, TyphoonService typhoonService, AmapClient amapClient, OpenTopoDataClient openTopoDataClient) {
        this.gisService = gisService;
        this.earthquakeService = earthquakeService;
        this.floodWarningService = floodWarningService;
        this.typhoonService = typhoonService;
        this.amapClient = amapClient;
        this.openTopoDataClient = openTopoDataClient;
    }

    /**
     * 获取空间数据
     * <p>
     * 获取空间数据列表，支持按类型和边界框进行过滤。
     * </p>
     *
     * @param type   数据类型（可选）
     * @param bounds 边界框，格式为 "minLng,minLat,maxLng,maxLat"（可选）
     * @param limit  返回数量限制（可选）
     * @param offset 偏移量（可选）
     * @return 空间数据列表
     */
    @GetMapping("/geo-data")
    public ApiResponse<List<GeoData>> getGeoData(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String bounds,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        List<GeoData> data = gisService.getGeoData(type, bounds, limit, offset);
        return ApiResponse.success(data);
    }

    /**
     * 获取图层数据
     * <p>
     * 根据图层 ID 获取对应的图层数据。
     * </p>
     *
     * @param id 图层 ID
     * @return 图层数据
     */
    @GetMapping("/layers/{id}")
    public ApiResponse<GeoData> getLayerData(@PathVariable String id) {
        GeoData data = gisService.getLayerData(id);
        return ApiResponse.success(data);
    }

    /**
     * 空间查询
     * <p>
     * 执行空间查询，根据几何对象查找相关的空间数据。
     * </p>
     *
     * @param request 空间查询请求，包含几何对象和查询参数
     * @return 查询结果列表
     */
    @PostMapping("/spatial-query")
    public ApiResponse<List<GeoData>> spatialQuery(@RequestBody SpatialQueryRequest request) {
        List<GeoData> data = gisService.spatialQuery(request);
        return ApiResponse.success(data);
    }

    /**
     * 空间分析
     * <p>
     * 执行空间分析操作，如缓冲区分析、相交分析等。
     * </p>
     *
     * @param type       分析类型
     * @param geometry   WKT 格式的几何对象
     * @param parameters 分析参数（可选）
     * @return 分析结果
     */
    @PostMapping("/spatial-analysis")
    public ApiResponse<Map<String, Object>> spatialAnalysis(
            @RequestParam String type,
            @RequestParam String geometry,
            @RequestBody(required = false) Map<String, Object> parameters) {
        Map<String, Object> result = gisService.spatialAnalysis(type, geometry, parameters);
        return ApiResponse.success(result);
    }

    /**
     * 地理编码
     * <p>
     * 将地址转换为经纬度坐标。
     * </p>
     *
     * @param address 地址
     * @return 包含经纬度的坐标信息
     */
    @GetMapping("/geocode")
    public ApiResponse<Map<String, Object>> geocode(@RequestParam String address) {
        Map<String, Object> result = gisService.geocode(address);
        return ApiResponse.success(result);
    }

    /**
     * 逆地理编码
     * <p>
     * 将经纬度坐标转换为地址。
     * </p>
     *
     * @param lnglat 经纬度坐标，格式为 "lng,lat"
     * @return 包含地址的信息
     */
    @GetMapping("/reverse-geocode")
    public ApiResponse<Map<String, Object>> reverseGeocode(@RequestParam String lnglat) {
        Map<String, Object> result = gisService.reverseGeocode(lnglat);
        return ApiResponse.success(result);
    }

    /**
     * 查询高程
     *
     * @param lng 经度
     * @param lat 纬度
     * @return 高程信息（单位：米）
     */
    @GetMapping("/elevation")
    public ApiResponse<Map<String, Object>> getElevation(@RequestParam double lng, @RequestParam double lat) {
        try {
            OpenTopoDataResponse response = openTopoDataClient.getElevation(lat + "," + lng);
            Map<String, Object> result = new HashMap<>();
            if (response != null && response.getResults() != null && !response.getResults().isEmpty()) {
                result.put("elevation", response.getResults().get(0).getElevation());
                return ApiResponse.success(result);
            }
            result.put("elevation", null);
            return ApiResponse.success(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("elevation", null);
            result.put("error", e.getMessage());
            return ApiResponse.success(result);
        }
    }

    /**
     * 获取地震预警数据
     *
     * @param startDate    开始日期（可选，格式 yyyy-MM-dd）
     * @param endDate      结束日期（可选，格式 yyyy-MM-dd）
     * @param minMagnitude 最小震级（可选）
     * @param maxMagnitude 最大震级（可选）
     * @param minDepth     最小深度km（可选）
     * @param maxDepth     最大深度km（可选）
     * @return 地震数据列表
     */
    @GetMapping("/earthquake")
    public ApiResponse<List<EarthquakeRecord>> getEarthquake(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) BigDecimal minMagnitude,
            @RequestParam(required = false) BigDecimal maxMagnitude,
            @RequestParam(required = false) BigDecimal minDepth,
            @RequestParam(required = false) BigDecimal maxDepth) {
        List<EarthquakeRecord> data;
        boolean hasFilters = startDate != null || minMagnitude != null || maxMagnitude != null || minDepth != null || maxDepth != null;
        if (hasFilters) {
            LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate + "T00:00:00") : LocalDateTime.now().minusDays(7);
            LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate + "T23:59:59") : LocalDateTime.now();
            data = earthquakeService.getEarthquakeByFilters(start, end, minMagnitude, maxMagnitude, minDepth, maxDepth);
        } else {
            data = earthquakeService.getEarthquakeByTimeRange("7d");
        }
        return ApiResponse.success(data);
    }

    /**
     * 获取洪水预警数据
     *
     * @param startDate 开始日期（可选，格式 yyyy-MM-dd）
     * @param endDate   结束日期（可选，格式 yyyy-MM-dd）
     * @return 洪水预警列表
     */
    @GetMapping("/flood-warning")
    public ApiResponse<List<FloodWarning>> getFloodWarning(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<FloodWarning> data;
        if (startDate != null) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate + "T23:59:59") : LocalDateTime.now();
            data = floodWarningService.getFloodWarningsByDateRange(start, end);
        } else {
            data = floodWarningService.getFloodWarningsByTimeRange("7d");
        }
        return ApiResponse.success(data);
    }

    /**
     * 获取台风列表
     *
     * @return 台风列表
     */
    @GetMapping("/typhoon")
    public ApiResponse<List<TyphoonRecord>> getTyphoon() {
        List<TyphoonRecord> data = typhoonService.getAllTyphoons();
        return ApiResponse.success(data);
    }

    /**
     * 获取当前活跃台风列表
     *
     * @return 活跃台风列表
     */
    @GetMapping("/typhoon/active")
    public ApiResponse<List<TyphoonRecord>> getActiveTyphoons() {
        List<TyphoonRecord> data = typhoonService.getActiveTyphoons();
        return ApiResponse.success(data);
    }

    /**
     * 获取台风数据的年份列表
     *
     * @return 年份列表
     */
    @GetMapping("/typhoon/years")
    public ApiResponse<List<Integer>> getTyphoonYears() {
        List<Integer> data = typhoonService.getTyphoonYears();
        return ApiResponse.success(data);
    }

    /**
     * 获取指定年份的台风列表
     *
     * @param year 年份
     * @return 台风列表
     */
    @GetMapping("/typhoon/year/{year}")
    public ApiResponse<List<TyphoonRecord>> getTyphoonsByYear(@PathVariable int year) {
        List<TyphoonRecord> data = typhoonService.getTyphoonsByYear(year);
        return ApiResponse.success(data);
    }

    /**
     * 获取台风轨迹点数据（从数据库读取）
     *
     * @param tfid 台风ID
     * @return 轨迹点列表
     */
    @GetMapping("/typhoon/{tfid}")
    public ApiResponse<List<TyphoonPoint>> getTyphoonPoints(@PathVariable String tfid) {
        List<TyphoonPoint> data = typhoonService.getTyphoonPoints(tfid);
        return ApiResponse.success(data);
    }

    /**
     * 高德输入提示（代理）
     */
    @GetMapping("/amap/inputtips")
    public ApiResponse<Object> amapInputtips(
            @RequestParam String keywords,
            @RequestParam(required = false, defaultValue = "") String city) {
        try {
            AmapInputTipsResponse response = amapClient.inputTips(amapKey, keywords, city, "all");
            return ApiResponse.success(response != null ? response.getTips() : null);
        } catch (Exception e) {
            return ApiResponse.error(500, "输入提示查询失败: " + e.getMessage());
        }
    }

    /**
     * 高德逆地理编码（代理）
     */
    @GetMapping("/amap/reverse-geocode")
    public ApiResponse<Object> amapReverseGeocode(@RequestParam String lnglat) {
        try {
            AmapRegeoResponse response = amapClient.reverseGeocode(amapKey, lnglat);
            return ApiResponse.success(response != null ? response.getRegeocode() : null);
        } catch (Exception e) {
            return ApiResponse.error(500, "逆地理编码失败: " + e.getMessage());
        }
    }

    /**
     * 高德行政区划查询（代理）
     */
    @GetMapping("/amap/district")
    public ApiResponse<Object> amapDistrict(
            @RequestParam String adcode,
            @RequestParam(required = false, defaultValue = "1") int subdistrict,
            @RequestParam(required = false, defaultValue = "base") String extensions) {
        try {
            AmapDistrictResponse response = amapClient.district(amapKey, adcode, subdistrict, extensions);
            return ApiResponse.success(response != null ? response.getDistricts() : null);
        } catch (Exception e) {
            return ApiResponse.error(500, "行政区划查询失败: " + e.getMessage());
        }
    }

    /**
     * 导出数据
     * <p>
     * 导出空间数据，支持多种格式。
     * </p>
     *
     * @param params 导出参数
     * @return 导出任务状态
     */
    @PostMapping("/export")
    public ApiResponse<Map<String, Object>> exportData(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "exporting");
        result.put("message", "数据导出任务已提交");
        return ApiResponse.success(result);
    }
}
