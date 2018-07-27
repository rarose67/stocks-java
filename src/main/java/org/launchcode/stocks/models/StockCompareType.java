package org.launchcode.stocks.models;

public enum StockCompareType {

    NONE ("None"),
    EQUAL ("="),
    LESS ("<"),
    GREATER (">");


    private final String name;

    StockCompareType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

