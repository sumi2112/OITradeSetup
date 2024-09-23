package com.shreehari.service;

import java.io.IOException;
import java.net.MalformedURLException;

public interface FIIDataService {
    void fetchPreviousData();
    void fecthDailyData() throws IOException;
}
