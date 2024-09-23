package com.shreehari.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ClientDetailsConfiguration {

    private String client_id;

    private String client_secret;

    private String redirectURI;

    @Autowired
    public void setClient_id(@Value("#{'${upstox.api_key}'}") String client_id) {
        this.client_id = client_id;
    }

    @Autowired
    public void setClient_secret(@Value("#{'${upstox.api_secret}'}") String client_secret) {
        this.client_secret = client_secret;
    }

    @Autowired
    public void setRedirectURI(@Value("#{'${redirectURI}'}") String redirectURI) {
        this.redirectURI = redirectURI;
    }
}
