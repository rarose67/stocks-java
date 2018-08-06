package org.launchcode.stocks.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.GregorianCalendar;
import java.util.List;

@Entity
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

    @NotNull
    @Min(value = 0)
    private double cash;

    @NotNull
    @Min(value = 0)
    private double balance;

    @NotNull
    private int years;

    @ManyToOne
    private User user;

    public Portfolio() {
        this.balance = 0;
        this.years = 0;
    }

    public Portfolio(@NotNull String name, double cash) {
        this();
        this.name = name;
        this.cash = cash;
        this.balance = cash;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public double getCash() {
        return cash;
    }

    public double getBalance() {
        return balance;
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

    public void calculate(int years) {

        if (this.years > 0)
        {
            reset();
        }

        GregorianCalendar day = (GregorianCalendar) GregorianCalendar.getInstance();
        GregorianCalendar finalDay = (GregorianCalendar) GregorianCalendar.getInstance();
        finalDay.add(GregorianCalendar.YEAR, years);
        int trading_days_this_week = 0;

        System.out.println("\nFinal Date: " + (finalDay.get(GregorianCalendar.MONTH)+1) +
                "/" + finalDay.get(GregorianCalendar.DATE) + "/" + finalDay.get(GregorianCalendar.YEAR));

        while (StockDateField.compare(day, finalDay) <= 0) {

            for (Position position : positions) {
                SimStock simStock = position.getSimStock();

                if (isMarketOpen(day)) {
                    simStock.trade();

                    if (StockDateField.compare(day, simStock.getNextDividendDate()) >= 0)
                    {
                        System.out.println("\n" + position.getSymbol() +
                                "\nDate: " + (day.get(GregorianCalendar.MONTH)+1) + "/" + day.get(GregorianCalendar.DATE) + "/" + day.get(GregorianCalendar.YEAR) +
                                "\nDivDate: " + simStock.showDate());
                        this.cash = position.quarter(this.cash);
                        simStock.nextDividendDate();
                    }
                }

                if (day.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY) {
                    simStock.adjustWVariance(trading_days_this_week);
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

        double balance = 0.0;

        for (Position position : positions)
        {
            balance += (position.getSimStock().getPrice() * position.getShares());
        }
        balance += this.cash;

        setBalance(balance);
    }

    public void reset()
    {
        for (Position position : positions)
        {
            position.getSimStock().reset();
        }
    }

}

