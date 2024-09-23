package com.shreehari.serviceimpl;

import com.shreehari.config.UrlConfiguration;
import com.shreehari.service.FIIDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FIIDataServiceImpl implements FIIDataService {

    @Autowired
    UrlConfiguration urlconfig;

    @Autowired
    WebClient webClient;

    @Override
    public void fecthDailyData() throws IOException {

        String uri = "https://nsearchives.nseindia.com/content/nsccl/fao_participant_oi_01032024.csv";
        String uri2= "https://www.nseindia.com/api/option-chain-indices?symbol=BANKNIFTY";

    }

    @Override
    public void fetchPreviousData() {
    }
}

