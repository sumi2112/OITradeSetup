package com.shreehari.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class UrlConfiguration {

    private String fiiURL;

    private String oiURL;

    private String upstoxBaseURL;

    private String authTokenURL;

    private String instrumentListURL;

    private String ltpURL;

    private String baseURL;

    private String fullMarketQuoteURL;

    public void setBaseURL(@Value("#{'${upstox.base.url}'}") String baseURL) {
        this.baseURL = baseURL;
    }

    @Autowired
    public void setFiiURL(@Value("#{'${nse.fii.url}'}") String fiiURL) {
        this.fiiURL = fiiURL;
    }

    @Autowired
    public void setOiURL(@Value("#{'${nse.oi.url}'}") String oiURL) {
        this.oiURL = oiURL;
    }

    @Autowired
    public void setUpstoxBaseURL(@Value("#{'${upstox.base.url}'}") String upstoxBaseURL) {
        this.upstoxBaseURL = upstoxBaseURL;
    }

    @Autowired
    public void setAuthTokenURL(@Value("#{'${upstox.oauth2.exchange.token.url}'}") String authTokenURL) {
        this.authTokenURL = authTokenURL;
    }

    @Autowired
    public void setInstrumentListURL(@Value("#{'${upstox.trading.instruments}'}") String instrumentListURL) {
        this.instrumentListURL = instrumentListURL;
    }

    @Autowired
    public void setLtpURL(@Value("#{'${upstox.trading.ltp.url}'}") String ltpURL) {
        this.ltpURL = ltpURL;
    }

    @Autowired
    public void setFullMarketQuoteURL(@Value("#{'${upstox.trading.full_market_quote}'}") String fullMarketQuoteURL) {
        this.fullMarketQuoteURL = fullMarketQuoteURL;
    }
}
