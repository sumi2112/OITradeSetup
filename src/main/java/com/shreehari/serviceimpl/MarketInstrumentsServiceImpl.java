package com.shreehari.serviceimpl;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.shreehari.config.UrlConfiguration;
import com.shreehari.models.FinancialInstumentsFromCSV;
import com.shreehari.service.MarketInstrumentsService;
import com.shreehari.utils.CurrentExpiryContractsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static com.shreehari.models.StringConstants.*;

@Service
public class MarketInstrumentsServiceImpl implements MarketInstrumentsService {

    private final Logger logger = LoggerFactory.getLogger(MarketInstrumentsServiceImpl.class);

    @Autowired
    UrlConfiguration urlConfig;
    String downloadPath = "C:\\Development\\OI_TradeSetupData\\downloadedFile.csv.gz";
    String extractedFilePath = "C:\\Development\\OI_TradeSetupData\\marketTradedInstrument.csv";

    @Override
    public Map<String, List<FinancialInstumentsFromCSV>> getConcernedFinancialInstruments() {

        Map<String, List<FinancialInstumentsFromCSV>> instrumentMap = new HashMap<>();

        String fileUrl = urlConfig.getInstrumentListURL();
        try {
            downloadFile(fileUrl, downloadPath);
            extractFile(downloadPath, extractedFilePath);
            List<FinancialInstumentsFromCSV> financialInstrumentsInTradeToday = listOfAllInstruments(extractedFilePath);

            CurrentExpiryContractsUtil contractFetcher = new CurrentExpiryContractsUtil();
            instrumentMap.putIfAbsent(BN_LTP_RES, contractFetcher.listOfBNCurrentExpiryContracts(financialInstrumentsInTradeToday));
            instrumentMap.putIfAbsent(NIFTY_50_LTP_RES, contractFetcher.listOfNiftyCurrentExpiryContracts(financialInstrumentsInTradeToday));
        } catch (IOException ex) {
            logger.error("Exception is" + Arrays.toString(ex.getStackTrace()));
        }
        return instrumentMap;
    }

    private List<FinancialInstumentsFromCSV> listOfAllInstruments(String file) throws IOException {

        Reader reader = new BufferedReader(new FileReader(file));
        CsvToBean<FinancialInstumentsFromCSV> csvReader = new CsvToBeanBuilder(reader)
                .withType(FinancialInstumentsFromCSV.class)
                .withSeparator(',')
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .build();
        return csvReader.parse();
    }

    private void downloadFile(String fileUrl, String downloadPath) throws IOException {
        URL url = new URL(fileUrl);
        try (InputStream in = url.openStream()) {
            Files.copy(in, Paths.get(downloadPath), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void extractFile(String inputFilePath, String outputFilePath) throws IOException {
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(inputFilePath));
             FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath)) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, len);
            }
        }
    }

}
