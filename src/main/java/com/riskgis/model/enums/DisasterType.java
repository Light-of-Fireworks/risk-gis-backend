package com.riskgis.model.enums;

public enum DisasterType {
    FLOOD("水灾"),
    HEAVY_RAIN("暴雨"),
    SNOWSTORM("雪灾"),
    HAIL("冰雹"),
    LIGHTNING("雷电"),
    EARTHQUAKE("地震"),
    TYPHOON("台风"),
    FLOOD_RIVER("洪水"),
    STORM_SURGE("风暴潮"),
    LANDSLIDE("滑坡-泥石流");

    private final String label;

    DisasterType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
