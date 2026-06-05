package com.riskgis.service;

import com.riskgis.model.TyphoonPoint;
import com.riskgis.model.TyphoonRecord;

import java.util.List;

public interface TyphoonService {

    /**
     * 从万维易源 342-3 接口同步当年所有台风列表，并对活跃台风调用详情接口入库
     */
    void syncTyphoonData();

    /**
     * 获取所有台风列表
     */
    List<TyphoonRecord> getAllTyphoons();

    /**
     * 获取单个台风的轨迹点数据（从数据库读取）
     *
     * @param tfid 台风ID
     * @return 轨迹点列表
     */
    List<TyphoonPoint> getTyphoonPoints(String tfid);

    /**
     * 获取当前活跃的台风列表
     */
    List<TyphoonRecord> getActiveTyphoons();

    /**
     * 获取台风数据的年份列表
     */
    List<Integer> getTyphoonYears();

    /**
     * 获取指定年份的台风列表
     */
    List<TyphoonRecord> getTyphoonsByYear(int year);
}
