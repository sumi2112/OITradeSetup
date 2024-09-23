package com.shreehari.utils;

import com.shreehari.models.FinancialInstumentsFromCSV;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.shreehari.models.StringConstants.*;

public class CurrentExpiryContractsUtil {

    public List<FinancialInstumentsFromCSV> listOfBNCurrentExpiryContracts(List<FinancialInstumentsFromCSV> financialInstumentsInTradeToday) {

        List<FinancialInstumentsFromCSV> bnInstruments = financialInstumentsInTradeToday.stream().filter(
                instrument -> instrument.getInstrument_type().equals(OPTION_INDEX) &&
                        instrument.getTradingsymbol().startsWith(BN)).collect(Collectors.toList());

        String latestExpiry = bnInstruments.stream().map(FinancialInstumentsFromCSV::getExpiry).distinct().sorted().toList().get(0);
        if (DateUtils.getIndiaDateToday().compareTo(latestExpiry) >0) {
            latestExpiry = bnInstruments.stream().map(FinancialInstumentsFromCSV::getExpiry).distinct().sorted().toList().get(1);
        }

        String expiry = latestExpiry;
        List<FinancialInstumentsFromCSV> finalList = bnInstruments.stream().
                filter(bnInstrument -> bnInstrument.getExpiry().equals(expiry)).collect(Collectors.toList());

        Collections.sort(finalList, Comparator.comparing(o -> (o.getInstrument_key() + o.getStrike())));
        return finalList;
    }

    public List<FinancialInstumentsFromCSV> listOfNiftyCurrentExpiryContracts(List<FinancialInstumentsFromCSV> financialInstumentsInTradeToday) {
        List<FinancialInstumentsFromCSV> niftyInstruments = financialInstumentsInTradeToday.stream().filter(
                instrument -> instrument.getInstrument_type().equals(OPTION_INDEX) &&
                        instrument.getTradingsymbol().startsWith(NIFTY)).collect(Collectors.toList());

        String latestExpiry = niftyInstruments.stream().map(FinancialInstumentsFromCSV::getExpiry).distinct().sorted().toList().get(0);

        List<FinancialInstumentsFromCSV> finalList = niftyInstruments.stream().
                filter(bnInstrument -> bnInstrument.getExpiry().equals(latestExpiry)).collect(Collectors.toList());
        Collections.sort(finalList, Comparator.comparing(o -> (o.getInstrument_key() + o.getStrike())));
        return finalList;
    }
}
