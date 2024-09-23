package com.shreehari.controllers;

import com.shreehari.service.ExchangeAccessTokenService;
import com.shreehari.utils.StaticDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/auth")
public class ExchangeAccessTokenController {

    @Autowired
    private ExchangeAccessTokenService exchangeAccessTokenService;

    @GetMapping("/accessToken")
    private ModelAndView acquireAccessToken(@RequestParam("code") String authCode) {

        StaticDataHolder.accessToken = exchangeAccessTokenService.fetchAccessToken(authCode);
        //return new ModelAndView("redirect:/market-data/getAllInstruments"); //ToDo:get all instruments and then derive CEs/PEs
        return new ModelAndView("redirect:/market-data/getAllInstruments");

    }

}
