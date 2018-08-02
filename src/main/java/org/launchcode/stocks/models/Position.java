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
    private SimStock stock;

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

    public Position(SimStock stock, int shares, int percentage, boolean reinvest) {
        this();
        this.symbol = stock.getSymbol();
        this.stock = stock;
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

    public SimStock getStock()
    {
        if (stock != null) {
            return stock;
        }
        else
        {
            SimStockData simStockData = SimStockData.getInstance();
            SimStock simStock = simStockData.findBySymbol(this.symbol);
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

    public void setStock(SimStock aStock) {
        this.stock = aStock;
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
}
