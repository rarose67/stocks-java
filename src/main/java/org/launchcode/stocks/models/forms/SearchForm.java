package org.launchcode.stocks.models.forms;

import org.launchcode.stocks.models.StockCompareType;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.GregorianCalendar;

public class SearchForm {

    private String symbol;
    private String name;
    private double price = -1.0;
    private double dividend = -1.0;
    private double yield = -1.0;

    @Min(0)
    @Max(31)
    private int day = 0;

    @Min(0)
    @Max(12)
    private int month = 0;

    private int year = 0;
    private GregorianCalendar divDate;

    private StockCompareType priceComp;
    private StockCompareType divComp;
    private StockCompareType yieldComp;
    private StockCompareType dateComp;

    public SearchForm() {
    }

    public SearchForm(String aSymbol, String aName, double aPrice, double aDividend, double aYield,
                      int aDay, int aMonth, int aYear, StockCompareType aPriceComp, StockCompareType aDivComp,
                      StockCompareType aYieldComp, StockCompareType aDateComp) {
        this.symbol = aSymbol;
        this.name = aName;
        this.price = aPrice;
        this.dividend = aDividend;
        this.yield = aYield;
        this.day = aDay;
        this.month = aMonth;
        this.year = aYear;
        this.divDate = new GregorianCalendar(year, (month-1), day);
        this.priceComp = aPriceComp;
        this.divComp = aDivComp;
        this.yieldComp = aYieldComp;
        this.dateComp = aDateComp;
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

    public double getDividend() {
        return dividend;
    }

    public void setDividend(double aDividend) {
        this.dividend = aDividend;
    }

    public double getYield() {
        return yield;
    }

    public void setYield(double aYield) {
        this.yield = aYield;
    }

    public GregorianCalendar getDivDate() {
        return divDate;
    }

    public void setDivDate(GregorianCalendar aDivDate) {
        this.divDate = aDivDate;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int aDay) {
        this.day = aDay;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int aMonth) {
        this.month = aMonth;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int aYear) {
        this.year = aYear;
    }

    public StockCompareType getPriceComp() {
        return priceComp;
    }

    public void setPriceComp(StockCompareType aPriceComp) {
        this.priceComp = aPriceComp;
    }

    public StockCompareType getDivComp() {
        return divComp;
    }

    public void setDivComp(StockCompareType aDivComp) {
        this.divComp = aDivComp;
    }

    public StockCompareType getYieldComp() {
        return yieldComp;
    }

    public void setYieldComp(StockCompareType aYieldComp) {
        this.yieldComp = aYieldComp;
    }

    public StockCompareType getDateComp() {
        return dateComp;
    }

    public void setDateComp(StockCompareType aDateComp) {
        this.dateComp = aDateComp;
    }

    public boolean compareSymbol(String aSymbol)
    {
        return (this.symbol.contains(aSymbol));
    }

    public boolean compareName(String aName)
    {
        return (this.name.contains(aName));
    }

    public boolean comparePrice(double value, StockCompareType type)
    {
        switch (type)
        {
            case EQUAL:
                return (price == value);
            case LESS:
                return (price < value);
            case GREATER:
                return (price > value);

        }

        throw new IllegalArgumentException("Cannot get field of type " + type);
    }

    public boolean compareDividend(double value, StockCompareType type)
    {
        switch (type)
        {
            case EQUAL:
                return (dividend == value);
            case LESS:
                return (dividend < value);
            case GREATER:
                return (dividend > value);

        }

        throw new IllegalArgumentException("Cannot get field of type " + type);
    }

    public boolean compareYield(double value, StockCompareType type)
    {
        switch (type)
        {
            case EQUAL:
                return (yield == value);
            case LESS:
                return (yield < value);
            case GREATER:
                return (yield > value);

        }

        throw new IllegalArgumentException("Cannot get field of type " + type);
    }

    public boolean compareDate(GregorianCalendar value, StockCompareType type)
    {
        int result;

        if (divDate.get(GregorianCalendar.YEAR) > value.get(GregorianCalendar.YEAR))
        {
            result = 1;
        }
        else if (divDate.get(GregorianCalendar.YEAR) < value.get(GregorianCalendar.YEAR))
        {
            result = -1;
        }
        else
        {
            result = 0;
        }

        if(result == 0)
        {
            if (divDate.get(GregorianCalendar.MONTH) > value.get(GregorianCalendar.MONTH))
            {
                result = 1;
            }
            else if (divDate.get(GregorianCalendar.MONTH) < value.get(GregorianCalendar.MONTH))
            {
                result = -1;
            }
            else
            {
                result = 0;
            }
        }

        if(result == 0)
        {
            if (divDate.get(GregorianCalendar.DAY_OF_MONTH) > value.get(GregorianCalendar.DAY_OF_MONTH))
            {
                result = 1;
            }
            else if (divDate.get(GregorianCalendar.DAY_OF_MONTH) < value.get(GregorianCalendar.DAY_OF_MONTH))
            {
                result = -1;
            }
            else
            {
                result = 0;
            }
        }

        switch (type)
        {
            case EQUAL:
                return (result == 0);
            case LESS:
                return (result < 0);
            case GREATER:
                return (result > 0);

        }

        throw new IllegalArgumentException("Cannot get field of type " + type);
    }
}

