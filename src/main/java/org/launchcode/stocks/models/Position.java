package org.launchcode.stocks.models;

import org.launchcode.stocks.models.data.SimStockData;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.GregorianCalendar;

@Entity
public class Position {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    private String symbol;

    @Transient
    private SimStock simStock;

    @NotNull
    @Min(0)
    private int shares;

    @NotNull
    @Min(0)
    @Max(100)
    private int percentage;

    @NotNull
    private boolean reinvest;

    @NotNull
    private int priority;

    @NotNull
    @Min(0)
    private int lastShares;

    @Min(0)
    private double lastFinalPrice;

    @NotNull
    @Min(0)
    @Max(100)
    private int lastPercentage;

    private boolean lastReinvest;

    private int lastPriority;

    @ManyToOne
    private Portfolio portfolio;

    public Position()
    {
        this.lastShares = 0;
        this.lastPercentage = 0;
        this.lastReinvest = false;
        this.lastPriority = 0;
        this.lastFinalPrice = 0.0;
    }

    public Position(SimStock simStock, int shares, int percentage, boolean reinvest, int priority) {
        this();
        this.symbol = simStock.getSymbol();
        this.simStock = simStock;
        this.shares = shares;
        this.percentage = percentage;
        this.reinvest = reinvest;
        this.priority = priority;
        this.lastPriority = this.priority;
    }

    public int getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public SimStock getSimStock()
    {
        if (simStock != null) {
            return this.simStock;
        }
        else
        {
            SimStockData simStockData = SimStockData.getInstance();
            SimStock simStock = simStockData.findBySymbol(this.symbol);
            this.simStock = simStock;
            return simStock;
        }
    }

    public int getShares() {
        return shares;
    }

    public int getPercentage() {
        return percentage;
    }

    public boolean isReinvest() {
        return reinvest;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setSymbol(String aSymbol) {
        this.symbol = aSymbol;
    }

    public void setPortfolio(Portfolio aPortfolio) {
        this.portfolio = aPortfolio;
    }

    public void setSimStock(SimStock aStock) {
        this.simStock = aStock;
        this.symbol = aStock.getSymbol();
    }

    public void setShares(int aShares) {
        this.shares = aShares;
    }

    public void setPercentage(int aPercentage) {
        this.percentage = aPercentage;
    }

    public void setReinvest(boolean aReinvest) {
        this.reinvest = aReinvest;
    }

    public int getPriority() {
        return priority;
    }

    public int getLastShares() {
        return lastShares;
    }

    public double getLastFinalPrice() {
        return SimStock.decimalPlaces(lastFinalPrice,2);
    }

    public int getLastPercentage() {
        return lastPercentage;
    }

    public boolean isLastReinvest() {
        return lastReinvest;
    }

    public int getLastPriority() {
        return lastPriority;
    }

    public void setPriority(int aPriority) {
        this.priority = aPriority;
    }

    public void setLastShares(int aLastShares) {
        this.lastShares = aLastShares;
    }

    public void setLastFinalPrice(double aLastFinalPrice) {
        this.lastFinalPrice = aLastFinalPrice;
    }

    public void setLastPercentage(int aLastPercentage) {
        this.lastPercentage = aLastPercentage;
    }

    public void setLastReinvest(boolean aLastReinvest) {
        this.lastReinvest = aLastReinvest;
    }

    public void setLastPriority(int aLastPriority) {
        this.lastPriority = aLastPriority;
    }

    /**
     * Recalculate the average change in the simStock's price after a given number of days.
     * @param days The
     */
    public void avg_change(int days)
    {
        double avgPrice;
        double numDays = (double) days;

        if (days >= 30)
        {
            this.getSimStock().adjustMVariance(numDays);
        }
        else
        {
            this.getSimStock().adjustWVariance(numDays);
        }
    }

    private void buyShares(int aShares)

    {
        if(aShares >= 0) {
            this.setShares(this.getShares() + aShares);
        }
    }

    public double quarter(double money)
    {
        double funds;
        double dividend = SimStock.decimalPlaces(((this.getSimStock().getqDividend()) * this.getShares()), 2);
        double newMoney = SimStock.decimalPlaces((money + dividend), 2);

        if (this.isReinvest() == false)
        {
            funds = newMoney * (this.getPercentage() / 100.0);
        }
        else
        {
            funds = dividend;
        }

        newMoney -= funds;
        System.out.println("\nYou have " + this.getShares() + " shares of " + this.getSymbol());

        int newShares = (int) (Math.floor(funds / simStock.getPrice()));
        buyShares(newShares);
        System.out.println("Bought " + newShares + " shares of " + this.getSymbol() +" at $"
                + simStock.getPrice() + " and now have " + this.getShares() + " shares.");

        newMoney += SimStock.decimalPlaces((funds - (newShares * simStock.getPrice())), 2);
        return newMoney;
    }

    protected void reset()
    {
        this.lastFinalPrice = getSimStock().getPrice();
        this.lastShares = this.shares;
        this.lastPercentage = this.percentage;
        this.lastReinvest = this.reinvest;
        this.lastPriority = this.priority;
        this.getSimStock().reset();
    }
}
