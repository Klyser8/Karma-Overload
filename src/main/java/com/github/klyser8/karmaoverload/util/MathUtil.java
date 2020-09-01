package com.github.klyser8.karmaoverload.util;

import java.util.Random;

public class MathUtil {

    private static final Random random = new Random();

    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }

    public static boolean calculateChance(double chance) {
        int maxBound = 100000;
        chance *= 100000;
        return random.nextInt(maxBound) < chance;
    }

}
