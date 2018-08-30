package org.launchcode.stocks.models;

import org.launchcode.stocks.models.data.StockData;

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

    /**
     * The function takes a double and shits the decimal point a given number of places.
     * @param num - The number to be shifted
     * @param places - the number of places to shit the decimal point.  A positive value shits to the right,
     *               A negative value shits to the left.
     * @return - the number after the shift
     */
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
        GregorianCalendar lastDividendDate = new GregorianCalendar(year, month, day);
        setNextDividendDate(lastDividendDate);
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

    public void setPrice(double aPrice)
    {
        if (aPrice > 0.0)
        {
            this.price = decimalPlaces(aPrice, 2);
            setYield();
        }
        else
        {
            this.price = 0.0;
        }
    }

    public double getVariance() {
        return decimalPlaces(variance,2);
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

    /**
     * return the nextDividenddate as a string.
     * @return
     */
    public String showDate()
    {
        String date = nextDividendDate.get(GregorianCalendar.MONTH)+1 + "/" +
                nextDividendDate.get(GregorianCalendar.DAY_OF_MONTH) + "/" +
                nextDividendDate.get(GregorianCalendar.YEAR);

        return date;
    }

    /**
     * Initialize the nextDividendDate field
     * @param aLastDividendDate - The previous dividend date
     */
    public void setNextDividendDate(GregorianCalendar aLastDividendDate)
    {
        GregorianCalendar today = (GregorianCalendar) GregorianCalendar.getInstance();

        this.nextDividendDate = aLastDividendDate;

        // While the next dividend date is prior to the current day, add 3 months to it.
        while (StockDateField.compare(this.nextDividendDate, today) < 0)
        {
            this.nextDividendDate.add(GregorianCalendar.MONTH, 3);
        }
    }

    /**
     * Add 3 months to the nextDividendDate
     */
    public void nextDividendDate()
    {
        this.nextDividendDate.add(GregorianCalendar.MONTH, 3);
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

    /**
     * Simulate a day of trading.
     */
    public void trade()
    {
        double start = - (this.getVariance());
        double range = this.getVariance() * 2.0;
        double change = start + (this.rand.nextDouble() * range);

        setPrice(getPrice() + change);
    }

    /**
     * Readjust the trading variance every month
     * @param days the number of days to average over.
     */
    public void adjustMVariance(double days)
    {
        double change = getPrice() - getMonthStartPrice();
        double month1ChangePerDay = change / days;
        setVariance(Math.abs(month1ChangePerDay));
        setMonthStartPrice(getPrice());
    }

    /**
     * Readjust the trading variance every week.
     * @param days the number of trading days to average over.
     */
    public void adjustWVariance(double days)
    {
        double change = getPrice() - getWeekStartPrice();
        double weekChangePerDay = change / days;
        if ((weekChangePerDay >= 0.01) || (weekChangePerDay <= -0.01)) {
            setVariance(Math.abs(weekChangePerDay));
        }
        else
        {
            setVariance(Math.abs(change));
        }
        this.setWeekStartPrice(getPrice());
    }

    /**
     * Reset the SimStock to it's inital values
     */
    protected void reset() {

        StockData stockData = StockData.getInstance();
        Stock stock = stockData.findBySymbol(this.symbol);

        int year = stock.getLastDividendDate().get(GregorianCalendar.YEAR);
        int month = stock.getLastDividendDate().get(GregorianCalendar.MONTH);
        int day = stock.getLastDividendDate().get(GregorianCalendar.DAY_OF_MONTH);

        setPrice(stock.getPrice());
        setVariance(stock.getVariance());
        setDividend(stock.getDividend());
        setqDividend(stock.getDividend() / 4.0);
        GregorianCalendar lastDividendDate = new GregorianCalendar(year, month, day);
        setNextDividendDate(lastDividendDate);
        setWeekStartPrice(stock.getWeekStartPrice());
        setMonthStartPrice(stock.getWeekStartPrice());
        setYield();
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
                ", nextDividendDate=" + showDate() +
                ", weekStartPrice=" + weekStartPrice +
                '}';
    }
}
