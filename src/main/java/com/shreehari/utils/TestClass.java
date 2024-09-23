package com.shreehari.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class TestClass {

    public static void main(String[] args) {
        final DecimalFormat df = new DecimalFormat("0.000");
        df.setRoundingMode(RoundingMode.DOWN);

        //System.out.printf("Hello and welcome!");
        long a = 10;
        long b = 387778;

        double c = (double)(a * 100) / b;
        System.out.println(""+null);

        System.out.println(df.format(c));


    }
}
