package org.launchcode.stocks.models.forms;

import org.launchcode.stocks.models.StockCompareType;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.GregorianCalendar;

public class PositionForm {

    @NotNull
    private String symbol;

    @NotNull
    @Min(0)
    private int shares;

    @NotNull
    @Min(0)
    @Max(100)
    private int percentage;

    @NotNull
    private boolean reinvest;

    public PositionForm() {
    }

    public PositionForm(@NotNull String symbol, int shares, int percentage, boolean reinvest) {
        this.symbol = symbol;
        this.shares = shares;
        this.percentage = percentage;
        this.reinvest = reinvest;
    }

    @NotNull
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(@NotNull String aSymbol) {
        this.symbol = aSymbol;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int aShares) {
        this.shares = aShares;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int aPercentage) {
        this.percentage = aPercentage;
    }

    public boolean isReinvest() {
        return reinvest;
    }

    public void setReinvest(boolean aReinvest) {
        this.reinvest = aReinvest;
    }
}

