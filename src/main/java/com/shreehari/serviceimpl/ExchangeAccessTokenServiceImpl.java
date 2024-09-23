package com.shreehari.serviceimpl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shreehari.config.ClientDetailsConfiguration;
import com.shreehari.config.UrlConfiguration;
import com.shreehari.service.ExchangeAccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ExchangeAccessTokenServiceImpl implements ExchangeAccessTokenService {

    @Autowired
    ClientDetailsConfiguration clientDetailsConfig;
    @Autowired
    WebClient webClient;
    @Autowired
    UrlConfiguration urlConfig;

    @Override
    public String fetchAccessToken(String authCode) {
        String response = webClient.post()
                .uri(urlConfig.getAuthTokenURL()) //fix to use base url
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .body(BodyInserters.fromFormData("code", authCode)
                        .with("client_id", clientDetailsConfig.getClient_id())
                        .with("client_secret", clientDetailsConfig.getClient_secret())
                        .with("redirect_uri", clientDetailsConfig.getRedirectURI())
                        .with("grant_type", "authorization_code"))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        return jsonObject.getAsJsonPrimitive("access_token").getAsString();
    }

}
