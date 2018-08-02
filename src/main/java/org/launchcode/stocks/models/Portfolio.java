package org.launchcode.stocks.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
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

    public Portfolio()
    {
        this.balance = 0;
        this.years = 0;
    }

    public Portfolio(@NotNull String name, double cash) {
        this() ;
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

    public void addItem(Position item)
    {
        this.positions.add(item);
    }

    public void removeItem(Position item)
    {
        this.positions.remove(item);
    }

    public void recalculate(int years)
    {
        for(int i=0; i < positions.size(); i++)
        {

        }
    }
}

