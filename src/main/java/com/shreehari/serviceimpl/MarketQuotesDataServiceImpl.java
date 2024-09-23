package com.shreehari.serviceimpl;

import com.shreehari.config.UrlConfiguration;
import com.shreehari.models.*;
import com.shreehari.service.MarketQuotesDataService;
import com.shreehari.utils.MarketDataProcessingUtil;
import com.shreehari.utils.OutputFileGenerator;
import com.shreehari.utils.StaticDataHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Set;

import static com.shreehari.models.StringConstants.BANK_NIFTY_LTP;
import static com.shreehari.models.StringConstants.NIFTY_50_LTP;

@Service
public class MarketQuotesDataServiceImpl implements MarketQuotesDataService {

    private final Logger logger = LoggerFactory.getLogger(MarketQuotesDataServiceImpl.class);

    @Autowired
    WebClient webClient;
    @Autowired
    UrlConfiguration urlConfig;

    @Override
    public void optionsMarketData(String accessToken) {
        Map<String, InstrumentLastTradedPrice> ltp_BN_Nifty = fetchLtp(String.join(",", NIFTY_50_LTP, BANK_NIFTY_LTP), accessToken);
       // Map<String, InstrumentLastTradedPrice> ltp_BN_Nifty = fetchLtp(String.join( BANK_NIFTY_LTP), accessToken);
        MarketDataProcessingUtil utils = new MarketDataProcessingUtil();
        Set<String> strikesToFetch = utils.findNearestStrikePrices(ltp_BN_Nifty, StaticDataHolder.currentExpiryActiveContracts);

        Map<String, InstrumentFullQuote> quote = getFullMarketQuotes(String.join(",", strikesToFetch), accessToken);
        Map<String, InstrumentQuote> finalQuote = utils.processQuote(quote);

        OutputFileGenerator.generateOutput(finalQuote, ltp_BN_Nifty);

    }

    //Fetching last traded price of Nifty and BankNifty(as of now).
    private Map<String, InstrumentLastTradedPrice> fetchLtp(String instruments, String accessToken) {
        InstrumentsLastTradedPriceResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(urlConfig.getLtpURL())
                        .queryParam("instrument_key", instruments)
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .retrieve()
                .bodyToMono(InstrumentsLastTradedPriceResponse.class)
                .block();
        return response.getData();
    }

    private Map<String, InstrumentFullQuote> getFullMarketQuotes(String contractsToFetch, String accessToken) {
        FullMarketQuoteResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(urlConfig.getFullMarketQuoteURL())
                        .queryParam("instrument_key", contractsToFetch)
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .retrieve()
                .bodyToMono(FullMarketQuoteResponse.class)
                .block();
        return response.getData();
    }


}
