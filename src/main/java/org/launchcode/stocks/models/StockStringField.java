package org.launchcode.stocks.models;

import java.util.Objects;

public class StockStringField extends StockField
{
    private String value;

    public StockStringField(StockFieldType aName, String aValue) {

        if ((aName == StockFieldType.SYMBOL) || (aName == StockFieldType.NAME))
        {
            super.setName(aName);
            this.value = aValue;
        }
        else
        {
            this.value = null;
        }
    }

    public boolean contains(String stored) {
        return stored.toLowerCase().contains(this.value.toLowerCase());
    }

    public String getValue() {
        return value;
    }

    public void setValue(String aValue) {
        value = aValue;
    }

    @Override
    public StockFieldType getName() {
        return super.getName();
    }

    @Override
    public void setName(StockFieldType aName)
    {
        switch(aName) {
            case SYMBOL:
                super.setName(aName);
            case NAME:
                super.setName(aName);
        }

        throw new IllegalArgumentException("Cannot get field of type " + aName);
    }

    @Override
    public String toString() {
        return "StockStringField {" +
                "name= " + getName() +
                ", value= " + value+
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockStringField)) return false;
        StockStringField that = (StockStringField) o;
        return getName() == that.getName() &&
                getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getName(), getValue());
    }
}
