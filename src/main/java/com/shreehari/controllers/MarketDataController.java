package com.shreehari.controllers;

import com.shreehari.service.MarketInstrumentsService;
import com.shreehari.service.MarketQuotesDataService;
import com.shreehari.utils.StaticDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import static com.shreehari.models.StringConstants.SUCCESS;
import static com.shreehari.utils.StaticDataHolder.accessToken;
import static com.shreehari.utils.StaticDataHolder.currentExpiryActiveContracts;

@RestController
@RequestMapping("/market-data")
public class MarketDataController {

    @Autowired
    MarketQuotesDataService marketQuotesDataService;

    @Autowired
    MarketInstrumentsService marketInstrumentsService;

    @GetMapping("/GetMarketData")
    private String getMarketData() {
        long start = System.currentTimeMillis();
        marketQuotesDataService.optionsMarketData(accessToken);
        long finish = System.currentTimeMillis();
        System.out.println("****************************Time Taken:" + (finish - start));
        return SUCCESS;
    }

    @GetMapping("/getAllInstruments")
    private ModelAndView getAllInstruments() {
        currentExpiryActiveContracts = marketInstrumentsService.getConcernedFinancialInstruments();
        return new ModelAndView("redirect:/market-data/GetMarketData");
    }
}
