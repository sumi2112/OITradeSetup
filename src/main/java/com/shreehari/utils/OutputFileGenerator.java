package com.shreehari.utils;

import com.shreehari.models.InstrumentLastTradedPrice;
import com.shreehari.models.InstrumentQuote;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.shreehari.models.StringConstants.*;
import static com.shreehari.utils.DateUtils.currentTime;
import static com.shreehari.utils.DateUtils.getDateToday;

public class OutputFileGenerator {


    static Map<String, Integer> previousTotalsBN = new HashMap<>();

    static Map<String, Integer> previousTotalsNIFTY = new HashMap<>();

    static Map<String, String> totalsPatternMap_BN = new HashMap<>();

    static Map<String, String> totalsPatternMap_Nifty = new HashMap<>();

    private static final DecimalFormat df = new DecimalFormat("0.00");

    private static final DecimalFormat df_1 = new DecimalFormat("0.0");
    static String tableFormat = "│%-11s│%-11s│%-7s│%-8s│%-17s│%-11s│%-7s│%-8s│%-17s│%-12s│%-7s│%-8s│%-17s│%-8s│%-15s│%-17s│%-5s│";

    //finalQuote has 92 elements, 46 each for BN and Nifty.
    public static void generateOutput(Map<String, InstrumentQuote> finalQuote, Map<String, InstrumentLastTradedPrice> ltp) {

        Map<String, InstrumentQuote> bn = new HashMap<>();
        Map<String, InstrumentQuote> nifty = new HashMap<>();
        df.setRoundingMode(RoundingMode.DOWN);
        df_1.setRoundingMode(RoundingMode.UP);

        double bn_ltp = 0.0;
        double nify_ltp = 0.0;

        for (String ltp_key : ltp.keySet()) {
            if (ltp_key.contains("Nifty Bank")) {
                bn_ltp = ltp.get(ltp_key).getLast_price();
            }
            if (ltp_key.contains("Nifty 50")) {
                nify_ltp = ltp.get(ltp_key).getLast_price();
            }
        }

        setSymbolInInstruments(finalQuote, bn_ltp, nify_ltp, bn, nifty);

        List<String> allBNKeys = new ArrayList<>(bn.keySet());
        Collections.sort(allBNKeys, Collections.reverseOrder());
        List<String> keyListBN = allBNKeys.subList(4, 42);
        List<String> allNiftyKeys = new ArrayList<>(nifty.keySet());
        Collections.sort(allNiftyKeys, Collections.reverseOrder());
        List<String> keyListNifty = allNiftyKeys.subList(4, 42);

        Map<String, Integer> bnTotals = calculateTotal(keyListBN, bn);
        Map<String, Integer> niftyTotals = calculateTotal(keyListNifty, nifty);

        print(keyListBN, bn, bnTotals, bn_ltp, "Nifty Bank", previousTotalsBN, totalsPatternMap_BN);
        print(keyListNifty, nifty, niftyTotals, nify_ltp, "Nifty 50", previousTotalsNIFTY, totalsPatternMap_Nifty);
        previousTotalsBN = bnTotals;
        previousTotalsNIFTY = niftyTotals;
    }

    private static void setSymbolInInstruments(Map<String, InstrumentQuote> finalQuote, double bn_ltp, double nify_ltp,
                                               Map<String, InstrumentQuote> bn, Map<String, InstrumentQuote> nifty) {
        for (String stock : finalQuote.keySet()) {
            if (stock.contains("NSE_FO:BANKNIFTY")) {
                InstrumentQuote quo = finalQuote.get(stock);
                int ltp_size = String.valueOf((int) bn_ltp).length();
                quo.setSymbol(stock.substring(stock.length() - (ltp_size + 2)).replaceFirst("(\\p{Lu})", " $1"));
                bn.putIfAbsent(stock, quo);
            } else if (stock.contains("NSE_FO:NIFTY")) {
                InstrumentQuote quo = finalQuote.get(stock);
                int ltp_size = String.valueOf((int) nify_ltp).length();
                quo.setSymbol(stock.substring(stock.length() - (ltp_size + 2)).replaceFirst("(\\p{Lu})", " $1"));
                nifty.putIfAbsent(stock, quo);
            }
        }
    }

    private static Map<String, Integer> calculateTotal(List<String> keys, Map<String, InstrumentQuote> instrument) {
        Map<String, Integer> totals = new HashMap<>();
        int CETotalBuy = 0, CETotalSell = 0, PETotalBuy = 0, PETotalSell = 0, CEOITotal = 0, PEOITotal = 0, max_buy_ce = 0,
                max_sell_ce = 0, max_buy_pe = 0, max_sell_pe = 0, oi_total_change = 0, buy_total_change_ce = 0, sell_total_change_ce = 0,
                buy_total_change_pe = 0, sell_total_change_pe = 0;
        String max_sell_key_ce = "", max_buy_key_ce = "", max_sell_key_pe = "", max_buy_key_pe = "";
        for (String key : keys) {
            if (key.contains("CE")) {
                int b = (int) instrument.get(key).getTotal_buy_quantity();
                int s = (int) instrument.get(key).getTotal_sell_quantity();
                int oic = (int) instrument.get(key).getTotalOIChange();
                int bc = (int) instrument.get(key).getTotalBuyChange();
                int sc = (int) instrument.get(key).getTotalSellChange();

                CETotalBuy += b;
                CETotalSell += s;
                oi_total_change += oic;
                buy_total_change_ce += bc;
                sell_total_change_ce += sc;

                CEOITotal += instrument.get(key).getOi();
                if (max_buy_ce < b) {
                    max_buy_ce = b;
                    max_buy_key_ce = key;
                }
                if (max_sell_ce < s) {
                    max_sell_ce = s;
                    max_sell_key_ce = key;
                }
            }
            if (key.contains("PE")) {
                int b = (int) instrument.get(key).getTotal_buy_quantity();
                int s = (int) instrument.get(key).getTotal_sell_quantity();
                int oic = (int) instrument.get(key).getTotalOIChange();
                int bc = (int) instrument.get(key).getTotalBuyChange();
                int sc = (int) instrument.get(key).getTotalSellChange();

                PETotalBuy += b;
                PETotalSell += s;
                oi_total_change += oic;
                buy_total_change_pe += bc;
                sell_total_change_pe += sc;

                PEOITotal += instrument.get(key).getOi();
                if (max_buy_pe < b) {
                    max_buy_pe = b;
                    max_buy_key_pe = key;
                }
                if (max_sell_pe < s) {
                    max_sell_pe = s;
                    max_sell_key_pe = key;
                }
            }
        }
        totals.put("CETotalBuy", CETotalBuy);
        totals.put("CETotalSell", CETotalSell);
        totals.put("PETotalBuy", PETotalBuy);
        totals.put("PETotalSell", PETotalSell);
        totals.put("CEOITotal", CEOITotal);
        totals.put("PEOITotal", PEOITotal);
        totals.put("AllTotalBuy", CETotalBuy + PETotalBuy);
        totals.put("AllTotalSell", CETotalSell + PETotalSell);
        totals.put("AllOI", CEOITotal + PEOITotal);
        totals.put("oi_total_change", oi_total_change);
        totals.put("buy_total_change_ce", buy_total_change_ce);
        totals.put("sell_total_change_ce", sell_total_change_ce);
        totals.put("buy_total_change_pe", buy_total_change_pe);
        totals.put("sell_total_change_pe", sell_total_change_pe);
        totals.put(max_buy_key_ce, 2);
        totals.put(max_sell_key_ce, 4);
        totals.put(max_buy_key_pe, 1);
        totals.put(max_sell_key_pe, 3);
        return totals;
    }

    private static void print(List<String> keys, Map<String, InstrumentQuote> instrument, Map<String, Integer> total,
                              double last_price, String market, Map<String, Integer> previousTotals, Map<String, String> totalsPatternMap) {

        StringBuffer sb = new StringBuffer();

        /*
         * BP is BuyingPercent - For CEs it is percent of ce out of total ce so does for PE. SP is vice versa.
         * BCQ is change in buyers quantity from previous buy quantity, same lines for SCQ and OCQ
         * BSR- Buy to Sell Ratio
         * BC is buyer change from start till now. Same for SC and OIC.
         * */
        sb.append("┌───────────┬───────────┬───────┬────────┬─────────────────┬───────────┬───────┬────────┬─────────────────┬────────────┬───────┬────────┬─────────────────┬────────┬───────────────┬─────────────────┬─────┐");
        sb.append("\n");
        sb.append("│ CMP " + (int) last_price + " │Buyer(in k)│  BC   │ BCQ(k) │  Buyer_Change   │  Seller(k)│  SC   │ SCQ(k) │  Seller_Change  │ OI(in 10K) │  OC   │ OCQ(k) │    OI_Change    │  LTP   │   BUILD_UP    │    PriceActn    │ BSR │");
        sb.append("\n");
        sb.append("├───────────┼───────────┼───────┼────────┼─────────────────┼───────────┼───────┼────────┼─────────────────┼────────────┼───────┼────────┼─────────────────┼────────┼───────────────┼─────────────────┼─────┤");
        sb.append("\n");

        List<String> ceKeys = keys.stream().filter(str -> str.contains("CE")).collect(Collectors.toList());
        List<String> peKeys = keys.stream().filter(str -> str.contains("PE")).collect(Collectors.toList());

        //Generating CE records
        generateRecords(ceKeys, sb, instrument, total, "AllTotalBuy", "AllTotalSell", "AllOI");

        //Inserting CE Total Columns
        int sell_qty_change_prcnt_ce = getTotalsDiff(total.get("CETotalSell"), previousTotals.get("CETotalSell"));
        int buy_qty_change_prcnt_ce = getTotalsDiff(total.get("CETotalBuy"), previousTotals.get("CETotalBuy"));
        int oi_qty_change_prcnt_ce = getTotalsDiff(total.get("CEOITotal"), previousTotals.get("CEOITotal"));
        String sell_total_pattern_ce = getPattern(total.get("CETotalSell"), previousTotals.get("CETotalSell"));
        String buy_total_pattern_ce = getPattern(total.get("CETotalBuy"), previousTotals.get("CETotalBuy"));
        String oi_total_pattern_ce = getPattern(total.get("CEOITotal"), previousTotals.get("CEOITotal"));
        sb.append(String.format(tableFormat,
                "Total CE",
                changeToThousand(total.get("CETotalBuy")) + "(" + df.format(((double) total.get("CETotalBuy") * 100.00) / ((double) total.get("CETotalBuy") + (double) total.get("PETotalBuy"))) + ")",
                //df.format(((double) total.get("CETotalBuy") * 100.00) / ((double) total.get("CETotalBuy") + (double) total.get("PETotalBuy"))),
                changeToThousand(total.get("buy_total_change_ce")),
                changeToThousand(buy_qty_change_prcnt_ce),
                getTotalsPattern(buy_total_pattern_ce, "buyTotalsPattern_ce", totalsPatternMap),
                changeToThousand(total.get("CETotalSell")) + "(" + df.format(((double) total.get("CETotalSell") * 100.00) / ((double) total.get("CETotalSell") + (double) total.get("PETotalSell"))) + ")",
                //df.format(((double) total.get("CETotalSell") * 100.00) / ((double) total.get("CETotalSell") + (double) total.get("PETotalSell"))),
                changeToThousand(total.get("sell_total_change_ce")),
                changeToThousand(sell_qty_change_prcnt_ce),
                getTotalsPattern(sell_total_pattern_ce, "sellTotalsPattern_ce", totalsPatternMap),
                changeTo10k(total.get("CEOITotal")) + "(" + df.format(((double) total.get("CEOITotal") * 100.00) / ((double) total.get("CEOITotal") + (double) total.get("PEOITotal"))) + ")",
                //df.format(((double) total.get("CEOITotal") * 100.00) / ((double) total.get("CEOITotal") + (double) total.get("PEOITotal"))),
                "",
                changeTo10k(oi_qty_change_prcnt_ce),
                getTotalsPattern(oi_total_pattern_ce, "oiTotalsPattern_ce", totalsPatternMap),
                "", "", "",
                changeToThousand((total.get("CETotalBuy")) - (total.get("CETotalSell")))));
        sb.append("\n");
        sb.append("├───────────┼───────────┼───────┼────────┼─────────────────┼───────────┼───────┼────────┼─────────────────┼────────────┼───────┼────────┼─────────────────┼────────┼───────────────┼─────────────────┼─────┤");
        sb.append("\n");

        //Generating PE records
        generateRecords(peKeys, sb, instrument, total, "AllTotalBuy", "AllTotalSell", "AllOI");

        //Generating PE Total records
        int sell_qty_change_prcnt_pe = getTotalsDiff(total.get("PETotalSell"), previousTotals.get("PETotalSell"));
        int buy_qty_change_prcnt_pe = getTotalsDiff(total.get("PETotalBuy"), previousTotals.get("PETotalBuy"));
        int oi_qty_change_prcnt_pe = getTotalsDiff(total.get("PEOITotal"), previousTotals.get("PEOITotal"));
        String sell_total_pattern_pe = getPattern(total.get("PETotalSell"), previousTotals.get("PETotalSell"));
        String buy_total_pattern_pe = getPattern(total.get("PETotalBuy"), previousTotals.get("PETotalBuy"));
        String oi_total_pattern_pe = getPattern(total.get("PEOITotal"), previousTotals.get("PEOITotal"));
        sb.append(String.format(tableFormat,
                "Total PE",
                changeToThousand(total.get("PETotalBuy")) + "(" + df.format(((double) total.get("PETotalBuy") * 100.00) / ((double) total.get("CETotalBuy") + (double) total.get("PETotalBuy"))) + ")",
                //df.format(((double) total.get("PETotalBuy") * 100.00) / ((double) total.get("CETotalBuy") + (double) total.get("PETotalBuy"))),
                changeToThousand(total.get("buy_total_change_pe")),
                changeToThousand(buy_qty_change_prcnt_pe),
                getTotalsPattern(buy_total_pattern_pe, "buyTotalsPattern_pe", totalsPatternMap),
                changeToThousand(total.get("PETotalSell")) + "(" + df.format(((double) total.get("PETotalSell") * 100.00) / ((double) total.get("CETotalSell") + (double) total.get("PETotalSell"))) + ")",
                //df.format(((double) total.get("PETotalSell") * 100.00) / ((double) total.get("CETotalSell") + (double) total.get("PETotalSell"))),
                changeToThousand(total.get("sell_total_change_pe")),
                changeToThousand(sell_qty_change_prcnt_pe),
                getTotalsPattern(sell_total_pattern_pe, "sellTotalsPattern_pe", totalsPatternMap),
                changeTo10k(total.get("PEOITotal")) + "(" + df.format(((double) total.get("PEOITotal") * 100.00) / ((double) total.get("CEOITotal") + (double) total.get("PEOITotal"))) + ")",
                //df.format(((double) total.get("PEOITotal") * 100.00) / ((double) total.get("CEOITotal") + (double) total.get("PEOITotal"))),
                "",
                changeTo10k(oi_qty_change_prcnt_pe),
                getTotalsPattern(oi_total_pattern_pe, "oiTotalsPattern_pe", totalsPatternMap),
                "", "", "",
                changeToThousand( (total.get("PETotalBuy")) - (total.get("PETotalSell")))));
        sb.append("\n");
        sb.append("└───────────┴───────────┴───────┴────────┴─────────────────┴───────────┴───────┴────────┴─────────────────┴────────────┴───────┴────────┴─────────────────┴────────┴───────────────┴─────────────────┴─────┘");
        writeToFile(sb, market);

    }

    private static void generateRecords(List<String> keys, StringBuffer sb, Map<String, InstrumentQuote> instrument, Map<String, Integer> total,
                                        String totalBuy, String totalSell, String totalOI) {
        for (int i = 0; i < keys.size(); i++) {
            if (i == 9 || i == 10) {
                sb.append("│           │           │       │        │                 │           │       │        │                 │            │       │        │                 │        │               │                 │     │");
                sb.append("\n");
            }

            InstrumentQuote quote = instrument.get(keys.get(i));
            if (total.containsKey(keys.get(i))) {
                appendBuffer(sb, quote, total.get(keys.get(i)), df, total, totalOI);
            } else {
                sb.append(String.format(tableFormat,
                        quote.getSymbol(),
                        changeToThousand(quote.getTotal_buy_quantity()),
                        //df.format(((double) quote.getTotal_buy_quantity() * 100.00) / ((double) total.get(totalBuy))),//when it was percent
                        changeToThousand(quote.getTotalBuyChange()),
                        changeToThousand(quote.getBuy_quantity_change()),
                        quote.getBuy_quantity_change_pattern(),
                        changeToThousand(quote.getTotal_sell_quantity()),
                        //df.format(((double) quote.getTotal_sell_quantity() * 100.00) / ((double) total.get(totalSell))),
                        changeToThousand(quote.getTotalSellChange()),
                        changeToThousand(quote.getSell_quantity_change()),
                        quote.getSell_quantity_change_pattern(),
                        changeTo10k(quote.getOi()),
                        //df.format(((double) quote.getOi() * 100.00) / ((double) total.get(totalOI))),
                        changeTo10k(quote.getTotalOIChange()),
                        changeTo10k(quote.getOi_change()),
                        quote.getOi_change_pattern(),
                        quote.getLast_price(),
                        //changeTo10k(quote.getTotalOIChange()),
                        quote.getOi_price_indication(),
                        quote.getPrice_action(),
                        changeToThousand(quote.getTotal_buy_quantity() - quote.getTotal_sell_quantity())));
                        //quote.getBuy_to_sell_ratio()));
                sb.append("\n");
            }
        }
        sb.append("├───────────┼───────────┼───────┼────────┼─────────────────┼───────────┼───────┼────────┼─────────────────┼────────────┼───────┼────────┼─────────────────┼────────┼───────────────┼─────────────────┼─────┤");
        sb.append("\n");
    }

    private static void appendBuffer(StringBuffer sb, InstrumentQuote quote, Integer mark, DecimalFormat df, Map<String, Integer> total,
                                     String totalOI) {

        switch (mark) {
            case 1, 2: //Highest Buy PE/CE
                sb.append(String.format(tableFormat,
                        quote.getSymbol(),
                        changeToThousand(quote.getTotal_buy_quantity()) + " -H",
                        //df.format(((double) quote.getTotal_buy_quantity() * 100.00) / ((double) total.get(totalBuy))),//when it was percent
                        changeToThousand(quote.getTotalBuyChange()),
                        changeToThousand(quote.getBuy_quantity_change()),
                        quote.getBuy_quantity_change_pattern(),
                        changeToThousand(quote.getTotal_sell_quantity()),
                        // df.format(((double) quote.getTotal_sell_quantity() * 100.00) / ((double) total.get(totalSell))),//when it was percent
                        changeToThousand(quote.getTotalSellChange()),
                        changeToThousand(quote.getSell_quantity_change()),
                        quote.getSell_quantity_change_pattern(),
                        changeTo10k(quote.getOi()),
                        //df.format(((double) quote.getOi() * 100.00) / ((double) total.get(totalOI))),
                        changeTo10k(quote.getTotalOIChange()),
                        changeTo10k(quote.getOi_change()),
                        quote.getOi_change_pattern(),
                        quote.getLast_price(),
                        quote.getOi_price_indication(),
                        quote.getPrice_action(),
                        changeToThousand(quote.getTotal_buy_quantity() - quote.getTotal_sell_quantity())));
                        //quote.getBuy_to_sell_ratio()));
                break;
            case 3, 4: //Highest Sell PE/CE
                sb.append(String.format(tableFormat,
                        quote.getSymbol(),
                        changeToThousand(quote.getTotal_buy_quantity()),
                        //df.format(((double) quote.getTotal_buy_quantity() * 100.00) / ((double) total.get(totalBuy))), when it was buyPercent
                        changeToThousand(quote.getTotalBuyChange()),
                        changeToThousand(quote.getBuy_quantity_change()),
                        quote.getBuy_quantity_change_pattern(),
                        changeToThousand(quote.getTotal_sell_quantity()) + " -H",
                        //df.format(((double) quote.getTotal_sell_quantity() * 100.00) / ((double) total.get(totalSell))),when it was sell percent
                        changeToThousand(quote.getTotalSellChange()),
                        changeToThousand(quote.getSell_quantity_change()),
                        quote.getSell_quantity_change_pattern(),
                        changeTo10k(quote.getOi()),
                        //df.format(((double) quote.getOi() * 100.00) / ((double) total.get(totalOI))),
                        changeTo10k(quote.getTotalOIChange()),
                        changeTo10k(quote.getOi_change()),
                        quote.getOi_change_pattern(),
                        quote.getLast_price(),
                        quote.getOi_price_indication(),
                        quote.getPrice_action(),
                        changeToThousand(quote.getTotal_buy_quantity() - quote.getTotal_sell_quantity())));
                        //quote.getBuy_to_sell_ratio()));
                break;
            default:
                sb.append("\n");
        }
        sb.append("\n");
    }

    private static void writeToFile(StringBuffer sb, String market) {

        try (FileWriter f = new FileWriter(
                        "C:\\Development\\OI_TradeSetupData\\BN\\" + market + "_" + getDateToday() + ".log", java.nio.charset.StandardCharsets.UTF_8,
                true);
             BufferedWriter b = new BufferedWriter(f);
             PrintWriter p = new PrintWriter(b)) {
            sb.append("\n");
            p.println(market + "- " + currentTime() + " ---" + "U-LongBuild; L-LongCover; D-ShortBuild; S-ShortCover; O-Up; X-Down; S-Same");
            p.println(sb);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private static String getTotalsPattern(String pattern, String key, Map<String, String> totalsPatternMap) {
        String str = checkLength(totalsPatternMap.get(key));
        totalsPatternMap.put(key, str + pattern);
        return totalsPatternMap.get(key);
    }

    private static String getPattern(Integer latest, Integer old) {
        if (old == null) {
            return EMPTY_STRING;
        }
        if (latest > old) return UP;
        else if (latest < old) return DOWN;
        return SAME;
    }

    private static String checkLength(String previousChange) {
        if (previousChange == null) {
            return EMPTY_STRING;
        }
        if (previousChange.length() > 14) {
            return previousChange.substring(1);
        }
        return previousChange;
    }

    private static int getTotalsDiff(Integer newTotal, Integer oldTotal) {
        int val = 0;
        if (oldTotal != null) {
            val = (newTotal - oldTotal);
        }
        return val;
    }

    private static int changeToThousand(int input) {
        return input / 1000;
    }

    private static long changeToThousand(long input) {
        return input / 1000;
    }

    private static long changeTo10k(long input) {
        return input / 10000;
    }
}
