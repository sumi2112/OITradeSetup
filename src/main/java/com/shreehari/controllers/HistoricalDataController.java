package com.shreehari.controllers;

import com.shreehari.service.FIIDataService;
import com.shreehari.service.MarketQuotesDataService;
import org.springframework.beans.factory.annotation.Autowired;

public class HistoricalDataController {
    @Autowired
    FIIDataService fiiDataService;
    @Autowired
    MarketQuotesDataService marketQuotesDataService;
}
