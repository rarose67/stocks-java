package org.launchcode.stocks.models;

public class StockField
{
    private StockFieldType name;

    public StockField() {
    }

    public StockField(StockFieldType aName) {

        this.setName(aName);
    }

    public StockFieldType getName() {
        return name;
    }

    public void setName(StockFieldType aName)
    {
        this.name = aName;
    }

}