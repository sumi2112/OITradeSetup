package com.shreehari.models;

import lombok.Data;

import java.util.Map;

@Data
public class InstrumentsLastTradedPriceResponse {

    private String status;

    private Map<String, InstrumentLastTradedPrice> data;
}