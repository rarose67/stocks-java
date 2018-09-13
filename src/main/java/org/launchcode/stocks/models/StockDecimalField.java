package org.launchcode.stocks.models;

public class StockDecimalField extends StockField
{
    //private StockFieldType name;

    private StockCompareType compareType;

    private double value;

    public StockDecimalField(StockFieldType aName, StockCompareType aCompareType, double aValue) {

        if ((aName == StockFieldType.PRICE) || (aName == StockFieldType.DIVIDEND) || (aName == StockFieldType.YIELD))
        {
            super.setName(aName);
            this.compareType = aCompareType;
            this.value = aValue;
        }
        else
        {
            this.value = -1;
        }
    }

    @Override
    public StockFieldType getName() {
        return super.getName();
    }

    @Override
    public void setName(StockFieldType aName)
    {
        switch(aName) {
            case PRICE:
                super.setName(aName);
            case DIVIDEND:
                super.setName(aName);
            case YIELD:
                super.setName(aName);
        }

        throw new IllegalArgumentException("Cannot get field of type " + aName);
    }

    public StockCompareType getCompareType() {
        return compareType;
    }

    public double getValue() {
        return value;
    }

    public void setCompareType(StockCompareType aType) {

        if ((aType == StockCompareType.EQUAL) || (aType == StockCompareType.LESS) || (aType == StockCompareType.GREATER))
        {
            this.compareType = aType;
        }
        else
        {
            this.compareType = StockCompareType.NONE;
        }
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean compareValue(double stored)
    {
        double value = this.getValue();

        switch (this.compareType)
        {
            case EQUAL:
                return (stored == value);
            case LESS:
                return (stored < value);
            case GREATER:
                return (stored > value);
            case LESSEQ:
                return (stored <= value);
            case GREATEREQ:
                return (stored >= value);

        }

        throw new IllegalArgumentException("Cannot get field of type " + this.compareType);
    }

    @Override
    public String toString() {
        return "StockDecimalField {" +
                "name= " + getName() +
                ", compareType= " + compareType +
                ", value= " + value+
                '}';
    }
}
