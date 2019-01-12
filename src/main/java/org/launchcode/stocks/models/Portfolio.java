package org.launchcode.stocks.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * This class defines the portfolio object which contains information about a portfolio .
 */
@Entity
@Table(name = "portfolio")
public class Portfolio {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    @Size(min = 1, max = 15)
    private String name;

    @OneToMany
    @JoinColumn(name = "portfolio_id")
    private List<Position> positions;

    //The amount of money used to reinvest in stocks.
    @NotNull
    @Min(value = 0)
    private double cash;

    //The overall value af a portfolio
    @NotNull
    @Min(value = 0)
    private double balance;

    //The # of years for the latest projection of the portfolio
    @NotNull
    private int years;

    //The amount of money used to reinvest in stocks after the previous run.
    @Min(value = 0)
    private double lastCash;

    //The overall value af a portfolio after the previous run.
    @NotNull
    @Min(value = 0)
    private double lastBalance;

    //The # of years for the previous projection of the portfolio
    @NotNull
    private int lastYears;

    @ManyToOne
    private User user;

    /**
     * This function creates a portfolio object with default values.
     */
    public Portfolio() {
        this.balance = 0.0;
        this.years = 0;
        this.lastCash = 0.0;
        this.lastBalance = 0.0;
        this.lastYears = 0;
    }

    /**
     * This function creates a portfolio object.
     * @param name - The name of the portfolio.
     * @param cash - The amount of money used to reinvest in stocks.
     */
    public Portfolio(@NotNull String name, double cash) {
        this();
        this.name = name;
        this.cash = cash;
        this.balance = cash;
    }

    /**
     *
     * @return - The portfolio id
     */
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public double getCash()
    {
        return SimStock.decimalPlaces(cash,2);
    }

    public double getBalance() {
        return SimStock.decimalPlaces(balance,2);
    }

    public int getYears() {
        return years;
    }

    public void setName(@NotNull String aName) {
        this.name = aName;
    }

    public void setCash(double aCash) {
        this.cash = aCash;
    }

    public void setBalance(double aBalance) {
        this.balance = aBalance;
    }

    public void setYears(int aYears) {
        this.years = aYears;
    }

    public double getLastCash() {
        return SimStock.decimalPlaces(lastCash,2);
    }

    public void setLastCash(double aLastCash) {
        this.lastCash = aLastCash;
    }

    public double getLastBalance() {
        return SimStock.decimalPlaces(lastBalance,2);
    }

    public void setLastBalance(double aLastBalance) {
        this.lastBalance = aLastBalance;
    }

    public int getLastYears() {
        return lastYears;
    }

    public void setLastYears(int aLastYears) {
        this.lastYears = aLastYears;
    }

    public void setPositions(List<Position> aPositions) {
        this.positions = aPositions;
    }

    public void setUser(User aUser) {
        this.user = aUser;
    }

    public void addItem(Position item) {
        this.positions.add(item);
    }

    public void removeItem(Position item) {
        this.positions.remove(item);
    }

    private boolean isMarketOpen(GregorianCalendar date) {
        int doy = date.get(GregorianCalendar.DAY_OF_YEAR);
        int dow = date.get(GregorianCalendar.DAY_OF_WEEK);

        if (date.isLeapYear(date.get(GregorianCalendar.YEAR))) {
            if ((dow == GregorianCalendar.SATURDAY) || (dow == GregorianCalendar.SUNDAY) || (doy == 1) || (doy == 15) || (doy == 50) || (doy == 90)
                    || (doy == 149) || (doy == 186) || (doy == 247) || (doy == 327) || (doy == 360)) {
                return false;
            } else {
                return true;
            }
        } else {
            if ((dow == GregorianCalendar.SATURDAY) || (dow == GregorianCalendar.SUNDAY) || (doy == 1) || (doy == 15) || (doy == 50) || (doy == 89)
                    || (doy == 148) || (doy == 185) || (doy == 246) || (doy == 326) || (doy == 359)) {
                return false;
            } else {
                return true;
            }
        }
    }

    public void calcBalance()
    {
        double balance = 0.0;

        for (Position position : positions)
        {
            if (position.isValid() &&
                    ((position.getState() == PositionState.NEW) || (position.getState() == PositionState.ACTIVE))) {
                balance += (position.getSimStock().getPrice() * position.getShares());
            }
        }
        balance += getCash();

        setBalance(SimStock.decimalPlaces(balance,2));
    }

    public void calculate(int years, List<Position> orderedPositionList) {

        GregorianCalendar day = (GregorianCalendar) GregorianCalendar.getInstance();
        GregorianCalendar finalDay = (GregorianCalendar) GregorianCalendar.getInstance();
        finalDay.add(GregorianCalendar.YEAR, years);
        int trading_days_this_week = 0;

        System.out.println("\nFinal Date: " + (finalDay.get(GregorianCalendar.MONTH)+1) +
                "/" + finalDay.get(GregorianCalendar.DATE) + "/" + finalDay.get(GregorianCalendar.YEAR));

        while (StockDateField.compare(day, finalDay) <= 0) {

            for (Position position : orderedPositionList) {
                if (((position.getState() == PositionState.NEW)) || ((position.getState() == PositionState.ACTIVE)))
                {
                    SimStock simStock = position.getSimStock();

                    if (isMarketOpen(day)) {
                        simStock.trade();

                        if (StockDateField.compare(day, simStock.getNextDividendDate()) >= 0) {
                            System.out.println("\n" + position.getSymbol() +
                                    "\nDate: " + (day.get(GregorianCalendar.MONTH) + 1) + "/" + day.get(GregorianCalendar.DATE) + "/" + day.get(GregorianCalendar.YEAR) +
                                    "\nDivDate: " + simStock.showDate() +
                                    "   \nCash: $" + getCash());
                            this.cash = position.quarter(this.cash);
                            System.out.println("You have $" + getCash() + " left to invest");
                            simStock.nextDividendDate();
                        }
                    }

                    if ((day.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY) && (trading_days_this_week > 0)) {
                        //System.out.println("Before: " + simStock.getSymbol() + " at $" + simStock.getPrice() +
                          //      "\tVari: " + simStock.getVariance());
                        simStock.adjustWVariance(trading_days_this_week);
                        //System.out.println("After: " + simStock.getSymbol() + " at $" + simStock.getPrice());
                    }
                }
            }

            if (isMarketOpen(day)) {
                trading_days_this_week++;
            }
            else if (day.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY)
            {
                trading_days_this_week = 0;
            }

            day.add(GregorianCalendar.DATE, 1);
        }

        System.out.println("\nLast Date: " + (day.get(GregorianCalendar.MONTH)+1) +
                "/" + day.get(GregorianCalendar.DATE) + "/" + day.get(GregorianCalendar.YEAR));

        calcBalance();
    }



    public double getTotalIncome()
    {
        double income = 0.0;

        for (Position position : positions)
        {
            if (position.isValid()) {
                income += (position.getSimStock().getDividend() * position.getShares());
            }
        }

        return (SimStock.decimalPlaces(income,2));
    }

    public double getLastTotalIncome()
    {
        double income = 0.0;

        for (Position position : positions)
        {
            if (position.isValid()) {
                income += (position.getSimStock().getDividend() * position.getLastShares());
            }
        }

        return (SimStock.decimalPlaces(income,2));
    }

    public void reset()
    {
        this.setLastCash(this.getCash());
        this.setLastBalance(this.getBalance());
        this.setLastYears(this.getYears());

        for (Position position : this.getPositions())
        {
            if (position.isValid() && (position.getState() == PositionState.ACTIVE))
            {
                position.reset();
            }
        }
    }

    public void resetAfterDel()
    {
        this.setLastCash(this.getCash());
        this.setLastBalance(this.getBalance());
        this.setLastYears(this.getYears());

        for (Position position : this.getPositions())
        {
            if (position.isValid() && (position.getState() == PositionState.ACTIVE))
            {
                position.updateLast();
            }
        }
    }
}

