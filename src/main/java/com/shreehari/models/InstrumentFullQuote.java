package com.shreehari.models;

import lombok.Data;

import java.util.List;

@Data
public class InstrumentFullQuote {

    private Ohlc ohlc;
    private Depth depth;
    private String timestamp;
    private String instrument_token;
    private String symbol;
    private double last_price;
    private long volume;
    private double average_price;
    private long oi;
    private double net_change;
    private long total_buy_quantity;
    private long total_sell_quantity;
    private double lower_circuit_limit;
    private double upper_circuit_limit;
    private String last_trade_time;
    private long oi_day_high;
    private long oi_day_low;
}

@Data
class Ohlc {
    private double open;
    private double high;
    private double low;
    private double close;
}

@Data
class Depth {
    private List<Order> buy;
    private List<Order> sell;
}

@Data
class Order {
    private long quantity;
    private double price;
    private long orders;
}
