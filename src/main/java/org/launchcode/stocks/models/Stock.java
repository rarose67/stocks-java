package org.launchcode.stocks.models;

import java.util.GregorianCalendar;

public class Stock {

    private static int nextId = 1;

    private int id;

    private String symbol;

    private String name;

    private double price;

    private double variance;

    private double dividend;

    private double qDividend;

    private double yield;

    private GregorianCalendar lastDividendDate;

    private double weekStartPrice;

    public Stock() {
        id = nextId;
        nextId++;
    }

    public Stock(String aSymbol, String aName, double aDividend) {
        this();
        this.symbol = aSymbol;
        this.name = aName;
        this.dividend = aDividend;
        this.qDividend = (aDividend / 4.0);
    }

    public Stock(String aSymbol, String aName, double aPrice, double aVariance, double aDividend,
                 GregorianCalendar aLastDividendDate, double aWeekStartPrice) {
        this();
        this.symbol = aSymbol;
        this.name = aName;
        this.price = aPrice;
        this.variance = aVariance;
        this.dividend = aDividend;
        this.qDividend = (aDividend / 4.0);
        this.lastDividendDate = aLastDividendDate;
        this.weekStartPrice = aWeekStartPrice;
        this.yield = ((aDividend / aPrice) * 100);
    }

    public int getId() {
        return id;
    }

    public static int getNextId() {
        return nextId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String aSymbol) {
        this.symbol = aSymbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        this.name = aName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double aPrice) {
        this.price = aPrice;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double aVariance) {
        this.variance = aVariance;
    }

    public double getDividend() {
        return dividend;
    }

    public void setDividend(double aDividend) {
        this.dividend = aDividend;
    }

    public double getqDividend() {
        return qDividend;
    }

    public void setqDividend(double aqDividend) {
        this.qDividend = aqDividend;
    }

    public GregorianCalendar getLastDividendDate() {
        return lastDividendDate;
    }

    public String getDate()
    {
        String date = lastDividendDate.get(GregorianCalendar.MONTH)+1 + "/" +
                lastDividendDate.get(GregorianCalendar.DAY_OF_MONTH) + "/" +
                lastDividendDate.get(GregorianCalendar.YEAR);

        return date;
    }

    public void setLastDividendDate(GregorianCalendar aLastDividendDate) {
        this.lastDividendDate = aLastDividendDate;
    }

    public double getWeekStartPrice() {
        return weekStartPrice;
    }

    public void setWeekStartPrice(double aWeekStartPrice)
    {
        if (aWeekStartPrice < 0.0)
        {
            this.weekStartPrice = this.price;
        }
        else {
            this.weekStartPrice = aWeekStartPrice;
        }
    }

    public double getYield() {
        return yield;
    }

    public void setYield() {
        this.yield = ((this.dividend /this.price) * 100);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", daily variance=" + variance +
                ", yearly dividend=" + dividend +
                ", Quarterly Dividend=" + qDividend +
                ", Annual yield=" + yield +
                ", lastDividendDate=" + getDate() +
                ", weekStartPrice=" + weekStartPrice +
                '}';
    }
}
