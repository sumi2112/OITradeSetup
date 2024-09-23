package com.shreehari.models;

import lombok.Data;

import java.util.Map;

@Data
public class FullMarketQuoteResponse {

    private String status;

    private Map<String, InstrumentFullQuote> data;
}
