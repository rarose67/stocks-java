package org.launchcode.stocks.models;

import java.util.GregorianCalendar;
import java.util.Objects;

public class StockDateField extends StockField
{
    private StockCompareType compareType;

    private GregorianCalendar value;

    public StockDateField(StockFieldType aName, StockCompareType aCompareType, GregorianCalendar aValue) {

        if ((aName == StockFieldType.DATE))
        {
            super.setName(aName);
            this.compareType = aCompareType;
            this.value = aValue;
        }
        else
        {
            this.value = null;
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
            case DATE:
                super.setName(aName);
        }

        throw new IllegalArgumentException("Cannot get field of type " + aName);
    }

    public StockCompareType getCompareType() {
        return compareType;
    }

    public GregorianCalendar getValue() {
        return value;
    }

    public String getDate()
    {
        String date = value.get(GregorianCalendar.MONTH)+1 + "/" +
                value.get(GregorianCalendar.DAY_OF_MONTH) + "/" +
                value.get(GregorianCalendar.YEAR);

        return date;
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

    public void setValue(GregorianCalendar aValue) {
        this.value = aValue;
    }

    public void setValue(int month, int day, int year)
    {
        this.value = new GregorianCalendar(year, month-1, day);
    }

    public static int compare(GregorianCalendar value, GregorianCalendar date) {
        int result;

        if (value.get(GregorianCalendar.YEAR) > date.get(GregorianCalendar.YEAR)) {
            result = 1;
        } else if (value.get(GregorianCalendar.YEAR) < date.get(GregorianCalendar.YEAR)) {
            result = -1;
        } else {
            result = 0;
        }

        if (result == 0) {
            if (value.get(GregorianCalendar.MONTH) > date.get(GregorianCalendar.MONTH)) {
                result = 1;
            } else if (value.get(GregorianCalendar.MONTH) < date.get(GregorianCalendar.MONTH)) {
                result = -1;
            } else {
                result = 0;
            }
        }

        if (result == 0) {
            if (value.get(GregorianCalendar.DAY_OF_MONTH) > date.get(GregorianCalendar.DAY_OF_MONTH)) {
                result = 1;
            } else if (value.get(GregorianCalendar.DAY_OF_MONTH) < date.get(GregorianCalendar.DAY_OF_MONTH)) {
                result = -1;
            } else {
                result = 0;
            }
        }

        return result;
    }

    public boolean compareDate(GregorianCalendar stored)
    {
        int result = compare(stored, this.getValue());


        switch (this.compareType)
        {
            case EQUAL:
                return (result == 0);
            case LESS:
                return (result < 0);
            case LESSEQ:
                return (result <= 0);
            case GREATER:
                return (result > 0);
            case GREATEREQ:
                return (result >= 0);

        }

        throw new IllegalArgumentException("Cannot get field of type " + this.compareType);
    }


    @Override
    public String toString() {
        return "StockDateField {" +
                "name= " + getName() +
                ", compareType= " + compareType +
                ", value= " + value.get(GregorianCalendar.MONTH)+1 + "/" +
                value.get(GregorianCalendar.DAY_OF_MONTH) + "/" +
                value.get(GregorianCalendar.YEAR) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockDateField)) return false;
        StockDateField that = (StockDateField) o;
        return getName() == that.getName() &&
                getCompareType() == that.getCompareType() &&
                (StockDateField.compare(getValue(), that.getValue()) == 0);
    }

    @Override
    public int hashCode() {

        return Objects.hash(getName(), getCompareType(), getValue());
    }
}
