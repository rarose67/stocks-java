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
    private GregorianCalendar openDate;

    @ManyToOne
    private Portfolio portfolio;

    public Position() {
        this.openDate = (GregorianCalendar) GregorianCalendar.getInstance();
    }

    public Position(SimStock simStock, int shares, int percentage, boolean reinvest) {
        this();
        this.symbol = simStock.getSymbol();
        this.simStock = simStock;
        this.shares = shares;
        this.percentage = percentage;
        this.reinvest = reinvest;
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

    @NotNull
    public GregorianCalendar getOpenDate() {
        return openDate;
    }

    public void setOpenDate(@NotNull GregorianCalendar openDate) {
        this.openDate = openDate;
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

    private void buyShares(int shares)
    {
        this.setShares(this.getShares() + shares);
    }

    public double quarter(double money)
    {
        double funds;
        double newMoney = SimStock.decimalPlaces(((this.getSimStock().getqDividend()) * this.getShares()), 2);

        if (this.isReinvest() == false)
        {
            funds = (money + newMoney) * (this.getPercentage() / 100.0);
            money -= funds;
        }
        else
        {
            funds = newMoney;
        }

        int newShares = (int) (Math.floor(funds / simStock.getPrice()));
        buyShares(newShares);
        System.out.println("\nBought " + newShares + " shares of " + this.getSymbol() +" at $"
                + simStock.getPrice());

        money += SimStock.decimalPlaces((funds - (newShares * simStock.getPrice())), 2);
        return money;
    }
}
