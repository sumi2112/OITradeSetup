package com.shreehari.utils;

import com.shreehari.models.FinancialInstumentsFromCSV;
import com.shreehari.models.InstrumentQuote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticDataHolder {

    public static String accessToken;

    public static Map<String, List<FinancialInstumentsFromCSV>> currentExpiryActiveContracts;

    public static Map<String, InstrumentQuote> previousQuote = new HashMap<>();

}
