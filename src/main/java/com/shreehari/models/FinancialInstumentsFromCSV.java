package com.shreehari.models;

import lombok.Data;

@Data
public class FinancialInstumentsFromCSV {

    private String instrument_key;
    private long exchange_token;
    private String tradingsymbol;
    private String name;
    private double last_price;
    private String expiry;
    private double strike;
    private double tick_size;
    private long lot_size;
    private String instrument_type;
    private String option_type;
    private String exchange;

    @Override
    public String toString() {
        return "FinancialInstument{" +
                "instrument_key='" + instrument_key + '\'' +
                ", exchange_token='" + exchange_token + '\'' +
                ", tradingsymbol='" + tradingsymbol + '\'' +
                ", name='" + name + '\'' +
                ", last_price='" + last_price + '\'' +
                ", expiry='" + expiry + '\'' +
                ", strike='" + strike + '\'' +
                ", tick_size='" + tick_size + '\'' +
                ", lot_size='" + lot_size + '\'' +
                ", instrument_type='" + instrument_type + '\'' +
                ", option_type='" + option_type + '\'' +
                ", exchange='" + exchange + '\'' +
                '}';
    }
}
