package com.riskgis.dto.typhoon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TyphoonPointDTO {

    private String lat;
    private String lng;
    private String strong;
    private String power;
    private String speed;
    private String pressure;
    private String movedirection;
    private String movespeed;
    private String radius7;
    private String radius10;
    private String time;

    @JsonProperty("movedirection")
    public void setMoveDirection(String moveDirection) {
        this.movedirection = moveDirection;
    }

    @JsonProperty("movespeed")
    public void setMoveSpeed(String moveSpeed) {
        this.movespeed = moveSpeed;
    }
}