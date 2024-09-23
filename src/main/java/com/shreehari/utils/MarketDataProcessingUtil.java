package com.shreehari.utils;

import com.shreehari.models.FinancialInstumentsFromCSV;
import com.shreehari.models.InstrumentFullQuote;
import com.shreehari.models.InstrumentLastTradedPrice;
import com.shreehari.models.InstrumentQuote;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.shreehari.models.StringConstants.*;

public class MarketDataProcessingUtil {
    private Map<String, List<String>> nearestStrikePrices;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    private static boolean firstRun = true;

    //Returns the list of insturment keys for which market quotes needs to be fetched.
    public Set<String> findNearestStrikePrices(Map<String, InstrumentLastTradedPrice> instrumentLTP,
                                               Map<String, List<FinancialInstumentsFromCSV>> currentExpiryActiveContracts) {
        Set<String> strikesToFetch = new HashSet<>();
        for (String key : instrumentLTP.keySet()) {
            double last_price = instrumentLTP.get(key).getLast_price();
            int nearestStrikePoint = getNearestStrike(currentExpiryActiveContracts.get(key), last_price);
            int ceiling = nearestStrikePoint + 23;
            int floor = nearestStrikePoint - 23;
            strikesToFetch.addAll(currentExpiryActiveContracts.get(key).subList(floor, ceiling)
                    .stream().map(FinancialInstumentsFromCSV::getInstrument_key).collect(Collectors.toSet()));
        }
        return strikesToFetch;
    }

    //As financialInstumentsFromCSVS is sorted list.
    private int getNearestStrike(List<FinancialInstumentsFromCSV> financialInstumentsFromCSVS, double last_price) {
        int diff = Integer.MAX_VALUE;
        Collections.sort(financialInstumentsFromCSVS, Comparator.comparing(o -> (o.getTradingsymbol())));
        for (int i = 0; i < financialInstumentsFromCSVS.size(); i++) {
            if (diff < Math.abs((int) (last_price - financialInstumentsFromCSVS.get(i).getStrike()))) {
                return i;
            }
            diff = (int) (last_price - financialInstumentsFromCSVS.get(i).getStrike());
        }
        return 0;
    }

    public Map<String, InstrumentQuote> processQuote(Map<String, InstrumentFullQuote> quotes) {

        Map<String, InstrumentQuote> objMap = new HashMap<>();
        for (String str : quotes.keySet()) {

            InstrumentFullQuote latestQuote = quotes.get(str);

            InstrumentQuote quote = setFixedValues(latestQuote);

            //Setting computed values
            InstrumentQuote previousQuote = StaticDataHolder.previousQuote.get(str);
            if (isPreviousQuotePresent(previousQuote)) {
                quote.setSell_quantity_change_pattern(getSellQtyPattern(latestQuote, previousQuote));
                quote.setBuy_quantity_change_pattern(getBuyQtyPattern(latestQuote, previousQuote));
                quote.setOi_change_pattern(getOiChangePattern(latestQuote, previousQuote));
                quote.setOi_price_indication(getOiPriceIndication(latestQuote, previousQuote));
                quote.setPrice_action(getPriceActionIndication(latestQuote, previousQuote));

                long sell_qty_change = (latestQuote.getTotal_sell_quantity() - previousQuote.getTotal_sell_quantity());
                long buy_qty_change_prcnt =  (latestQuote.getTotal_buy_quantity() - previousQuote.getTotal_buy_quantity());
                long oi_change = (latestQuote.getOi() - previousQuote.getOi());

                long total_buy_change = (latestQuote.getTotal_buy_quantity() - previousQuote.getTotal_buy_quantity())+previousQuote.getTotalBuyChange();
                long total_sell_change = (latestQuote.getTotal_sell_quantity() - previousQuote.getTotal_sell_quantity())+previousQuote.getTotalSellChange();
                long total_oi_change = (latestQuote.getOi() - previousQuote.getOi())+ previousQuote.getOi_change();

                quote.setSell_quantity_change(sell_qty_change);
                quote.setBuy_quantity_change(buy_qty_change_prcnt);
                quote.setOi_change(oi_change);
                quote.setBuy_to_sell_ratio(buySellRatio(latestQuote));
                quote.setTotalOIChange(total_oi_change);
                quote.setTotalBuyChange(total_buy_change);
                quote.setTotalSellChange(total_sell_change);
            }
            objMap.putIfAbsent(str, quote);
        }
        StaticDataHolder.previousQuote = objMap;
        return objMap;
    }

    private String getPriceActionIndication(InstrumentFullQuote latestQuote, InstrumentQuote previousQuote) {

        String str = checkLength(previousQuote.getPrice_action());
        if (latestQuote.getOi() > previousQuote.getOi() && latestQuote.getLast_price() > previousQuote.getLast_price() /*&&
                latestQuote.getVolume() > previousQuote.getVolume()*/ /*&& (latestQuote.getTotal_buy_quantity() > previousQuote.getTotal_buy_quantity() ||
                latestQuote.getTotal_sell_quantity() < previousQuote.getTotal_sell_quantity())*/
        ) {
            str = str + long_build;
        } else if (latestQuote.getOi() > previousQuote.getOi() && latestQuote.getLast_price() < previousQuote.getLast_price() /*&&
                latestQuote.getVolume() > previousQuote.getVolume() *//*&& (latestQuote.getTotal_buy_quantity() < previousQuote.getTotal_buy_quantity()  ||
                latestQuote.getTotal_sell_quantity() > previousQuote.getTotal_sell_quantity())*/
        ) {
            str = str + short_build;
        } else if (latestQuote.getOi() < previousQuote.getOi() && latestQuote.getLast_price() > previousQuote.getLast_price() /*&&
                latestQuote.getVolume() < previousQuote.getVolume()*/ /*&& (latestQuote.getTotal_buy_quantity() > previousQuote.getTotal_buy_quantity()  ||
                latestQuote.getTotal_sell_quantity() < previousQuote.getTotal_sell_quantity())*/
        ) {
            str = str + short_cover;
        } else if (latestQuote.getOi() < previousQuote.getOi() && latestQuote.getLast_price() < previousQuote.getLast_price()/* &&
                latestQuote.getVolume() < previousQuote.getVolume()*/ /*&& (latestQuote.getTotal_buy_quantity() < previousQuote.getTotal_buy_quantity()  ||
                latestQuote.getTotal_sell_quantity() > previousQuote.getTotal_sell_quantity())*/
        ) {
            str = str + long_cover;
        }
        else str = str + " ";
        return str;
    }

    private String getSellQtyPattern(InstrumentFullQuote latestQuote, InstrumentQuote previousQuote) {
        String str = checkLength(previousQuote.getSell_quantity_change_pattern());
        if (latestQuote.getTotal_sell_quantity() > previousQuote.getTotal_sell_quantity()) str = str + UP;
        else if (latestQuote.getTotal_sell_quantity() < previousQuote.getTotal_sell_quantity()) str = str + DOWN;
        else str = str + SAME;
        return str;
    }

    private String getBuyQtyPattern(InstrumentFullQuote latestQuote, InstrumentQuote previousQuote) {
        String str = checkLength(previousQuote.getBuy_quantity_change_pattern());
        if (latestQuote.getTotal_buy_quantity() > previousQuote.getTotal_buy_quantity()) str = str + UP;
        else if (latestQuote.getTotal_buy_quantity() < previousQuote.getTotal_buy_quantity()) str = str + DOWN;
        else str = str + SAME;
        return str;
    }

    private String getOiChangePattern(InstrumentFullQuote latestQuote, InstrumentQuote previousQuote) {
        String str = checkLength(previousQuote.getOi_change_pattern());
        if (latestQuote.getOi() > previousQuote.getOi()) str = str + UP;
        else if (latestQuote.getOi() < previousQuote.getOi()) str = str + DOWN;
        else str = str + SAME;
        return str;
    }

    private String getOiPriceIndication(InstrumentFullQuote latestQuote, InstrumentQuote previousQuote) {
        if (latestQuote.getOi() > previousQuote.getOi() && latestQuote.getLast_price() > previousQuote.getLast_price() &&
                latestQuote.getVolume() > previousQuote.getVolume() && (latestQuote.getTotal_buy_quantity() > previousQuote.getTotal_buy_quantity() ||
                latestQuote.getTotal_sell_quantity() < previousQuote.getTotal_sell_quantity())
        ) {
            return LONG_BUILD;
        } else if (latestQuote.getOi() > previousQuote.getOi() && latestQuote.getLast_price() < previousQuote.getLast_price() &&
                latestQuote.getVolume() > previousQuote.getVolume() && (latestQuote.getTotal_buy_quantity() < previousQuote.getTotal_buy_quantity()  ||
                latestQuote.getTotal_sell_quantity() > previousQuote.getTotal_sell_quantity())
        ) {
            return SHORT_BUILD;
        } else if (latestQuote.getOi() < previousQuote.getOi() && latestQuote.getLast_price() > previousQuote.getLast_price() &&
                latestQuote.getVolume() < previousQuote.getVolume() && (latestQuote.getTotal_buy_quantity() > previousQuote.getTotal_buy_quantity()  ||
                latestQuote.getTotal_sell_quantity() < previousQuote.getTotal_sell_quantity())
        ) {
            return SHORT_COVER;
        } else if (latestQuote.getOi() < previousQuote.getOi() && latestQuote.getLast_price() < previousQuote.getLast_price() &&
                latestQuote.getVolume() < previousQuote.getVolume() && (latestQuote.getTotal_buy_quantity() < previousQuote.getTotal_buy_quantity()  ||
                latestQuote.getTotal_sell_quantity() > previousQuote.getTotal_sell_quantity())
        ) {
            return LONG_COVER;
        }
        return EMPTY_STRING;
    }

    private String buySellRatio(InstrumentFullQuote latestQuote) {
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(((double) latestQuote.getTotal_buy_quantity()/ (double) latestQuote.getTotal_sell_quantity()));
    }

    private String checkLength(String previousChange) {
        if (previousChange == null) {
            return EMPTY_STRING;
        }
        if (previousChange.length() > 14) {
            return previousChange.substring(1);
        }
        return previousChange;
    }

    private InstrumentQuote setFixedValues(InstrumentFullQuote latestQuote) {
        InstrumentQuote quote = new InstrumentQuote();
        quote.setTimestamp(latestQuote.getTimestamp());
        quote.setInstrument_token(latestQuote.getInstrument_token());
        quote.setLast_price(latestQuote.getLast_price());
        quote.setSymbol(latestQuote.getSymbol());
        quote.setVolume(latestQuote.getVolume());
        quote.setOi(latestQuote.getOi());
        quote.setTotal_buy_quantity(latestQuote.getTotal_buy_quantity());
        quote.setTotal_sell_quantity(latestQuote.getTotal_sell_quantity());
        quote.setLast_trade_time(latestQuote.getLast_trade_time());
        quote.setOi_day_high(latestQuote.getOi_day_high());
        quote.setOi_day_low(latestQuote.getOi_day_low());
        quote.setBuySellDiff(latestQuote.getTotal_buy_quantity() - latestQuote.getTotal_sell_quantity());

        return quote;
    }

    private boolean isPreviousQuotePresent(InstrumentQuote previousQuote) {
        if (previousQuote != null) {
            return true;
        }
        return false;
    }


}
