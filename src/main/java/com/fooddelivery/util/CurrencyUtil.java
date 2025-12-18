package com.fooddelivery.util;

public class CurrencyUtil {
    
    private static final String CURRENCY_SYMBOL = "ETB";
    private static final String CURRENCY_DISPLAY = "Birr";
    
    /**
     * Format amount as Ethiopian Birr
     */
    public static String format(double amount) {
        return String.format("%.2f %s", amount, CURRENCY_SYMBOL);
    }
    
    /**
     * Format amount with Birr symbol
     */
    public static String formatWithSymbol(double amount) {
        return String.format("%.2f %s", amount, CURRENCY_DISPLAY);
    }
    
    /**
     * Get currency symbol
     */
    public static String getSymbol() {
        return CURRENCY_SYMBOL;
    }
    
    /**
     * Get currency display name
     */
    public static String getDisplayName() {
        return CURRENCY_DISPLAY;
    }
}

