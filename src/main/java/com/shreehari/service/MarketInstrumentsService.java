package com.shreehari.service;

import com.shreehari.models.FinancialInstumentsFromCSV;

import java.util.List;
import java.util.Map;

public interface MarketInstrumentsService {

    Map<String, List<FinancialInstumentsFromCSV>> getConcernedFinancialInstruments();
}

