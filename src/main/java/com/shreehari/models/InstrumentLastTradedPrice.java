package com.shreehari.models;

import lombok.Data;

@Data
public class InstrumentLastTradedPrice {

    private double last_price;

    private String instrument_token;
}