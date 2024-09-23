package com.shreehari.models;

import lombok.Data;

@Data
public class InstrumentQuote {

    private String timestamp;
    private String instrument_token;
    private String symbol;
    private double last_price;
    private long volume;
    private long oi;
    private long total_buy_quantity;
    private long total_sell_quantity;
    private String last_trade_time;
    private long oi_day_high;
    private long oi_day_low;
    private long buySellDiff;

    private String sell_quantity_change_pattern;
    private String buy_quantity_change_pattern;
    private String oi_change_pattern;
    private long sell_quantity_change;
    private long buy_quantity_change;
    private long oi_change;

    private String oi_price_indication;
    private String buy_to_sell_ratio;

    private String price_action;

    private long totalOIChange;

    private long totalBuyChange;

    private long totalSellChange;

}
