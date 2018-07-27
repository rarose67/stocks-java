package org.launchcode.stocks.models;

import java.util.GregorianCalendar;
import java.util.Random;

public class SimStock {

    private static int nextId = 1;

    private int id;

    private String symbol;

    private String name;

    private double price;

    private double variance;

    private double dividend;

    private double qDividend;

    private double yield;

    private GregorianCalendar nextDividendDate;

    private double weekStartPrice;

    private double monthStartPrice;

    private Random rand;

    public static double decimalPlaces(double num, int places)
    {
        double multiplier = Math.pow(10.0, places);
        double dec =  Math.floor((num * multiplier) + 0.5) / multiplier;

        return dec;
    }

    public SimStock() {
        id = nextId;
        nextId++;
    }

    public SimStock(Stock stock) {
        this();

        int year = stock.getLastDividendDate().get(GregorianCalendar.YEAR);
        int month = stock.getLastDividendDate().get(GregorianCalendar.MONTH);
        int day = stock.getLastDividendDate().get(GregorianCalendar.DAY_OF_MONTH);

        this.symbol = stock.getSymbol();
        this.name = stock.getName();
        setPrice(stock.getPrice());
        setVariance(stock.getVariance());
        setDividend(stock.getDividend());
        setqDividend(stock.getDividend() / 4.0);
        this.nextDividendDate = new GregorianCalendar(year, month, day);
        nextDividendDate();
        setWeekStartPrice(stock.getWeekStartPrice());
        setMonthStartPrice(stock.getWeekStartPrice());
        setYield();
        this.rand = new Random();
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
        this.price = decimalPlaces(aPrice, 2);
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double aVariance) {
        this.variance = decimalPlaces(aVariance,2);
    }

    public double getDividend() {
        return dividend;
    }

    public void setDividend(double aDividend) {
        this.dividend = decimalPlaces(aDividend,2);
    }

    public double getqDividend() {
        return qDividend;
    }

    public void setqDividend(double aqDividend) {
        this.qDividend = decimalPlaces(aqDividend,2);
    }

    public GregorianCalendar getNextDividendDate() {
        return nextDividendDate;
    }

    public String getDate()
    {
        String date = nextDividendDate.get(GregorianCalendar.MONTH)+1 + "/" +
                nextDividendDate.get(GregorianCalendar.DAY_OF_MONTH) + "/" +
                nextDividendDate.get(GregorianCalendar.YEAR);

        return date;
    }

    public void setNextDividendDate(GregorianCalendar aLastDividendDate)
    {
        GregorianCalendar today = (GregorianCalendar) GregorianCalendar.getInstance();

        this.nextDividendDate = aLastDividendDate;

        while (this.nextDividendDate.before(today))
        {
            this.nextDividendDate.add(GregorianCalendar.MONTH, 3);
        }
    }

    public void nextDividendDate()
    {
        GregorianCalendar today = (GregorianCalendar) GregorianCalendar.getInstance();

        while (this.nextDividendDate.before(today))
        {
            this.nextDividendDate.add(GregorianCalendar.MONTH, 3);
        }
    }

    public double getWeekStartPrice() {
        return weekStartPrice;
    }

    public void setWeekStartPrice(double aWeekStartPrice) {
        this.weekStartPrice = decimalPlaces(aWeekStartPrice,2);
    }

    public double getMonthStartPrice() {
        return monthStartPrice;
    }

    public void setMonthStartPrice(double aMonthStartPrice) {
        this.monthStartPrice = decimalPlaces(aMonthStartPrice,2);
    }

    public double getYield() {
        return yield;
    }

    public void setYield() {
        this.yield = ((this.dividend /this.price) * 100);
        this.yield = decimalPlaces(this.yield, 2);
    }

    private void trade()
    {
        double start = - (this.getVariance());
        double range = this.getVariance() *2.0;
        double change = start + (this.rand.nextDouble() * range);

        setPrice(getPrice() + change);
    }

    public void adjustVariance()
    {
        double change = getPrice() - getMonthStartPrice();
        double month1ChangePercent = change / 30.0;
        setVariance(Math.abs(price * month1ChangePercent));
        setMonthStartPrice(getPrice());
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
                ", nextDividendDate=" + getDate() +
                ", weekStartPrice=" + weekStartPrice +
                '}';
    }
}
