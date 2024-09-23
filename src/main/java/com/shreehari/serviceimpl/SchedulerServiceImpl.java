package com.shreehari.serviceimpl;

import com.shreehari.service.MarketQuotesDataService;
import com.shreehari.utils.DateUtils;
import com.shreehari.utils.StaticDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulerServiceImpl {

    @Autowired
    MarketQuotesDataService marketQuotesService;

    @Scheduled(cron = "0 0/5 9-15 * * Mon-Fri", zone = "IST")
    public void schedule() {
        long start = System.currentTimeMillis();
        System.out.println("Scheduler Started");
        marketQuotesService.optionsMarketData(StaticDataHolder.accessToken);
        long finish = System.currentTimeMillis();
        System.out.println("****************************Time Taken:" + (finish - start) + " " + DateUtils.currentTime());

    }
}
