package org.launchcode.stocks.models.data;

import org.launchcode.stocks.models.*;

import java.util.ArrayList;
import java.util.HashMap;

public class StockData {

    private ArrayList<Stock> stocks = new ArrayList<>();
    private static StockData instance;

    public static double decimalPlaces(double num, int places)
    {
        double multiplier = Math.pow(10.0, places);
        double dec =  Math.floor((num * multiplier) + 0.5) / multiplier;

        return dec;
    }

    public HashMap<String, String> getSymbolsAndNames() {
        return StockDataImporter.getOfferedStocks();
    }

    private StockData() {
        StockDataImporter.loadSymbols(this);

    }

    public static StockData getInstance() {
        if (instance == null) {
            instance = new StockData();
        }
        return instance;
    }

    public Stock findById(int id) {
        for (Stock stock : stocks) {
            if (stock.getId() == id)
                return stock;
        }

        return null;
    }

    public Stock findBySymbol(String symbol) {
        for (Stock stock : stocks) {
            if (stock.getSymbol().equals(symbol))
                return stock;
        }

        return null;
    }

    public Stock findByName(String name) {
        for (Stock stock : stocks) {
            if (stock.getName().equals(name))
                return stock;
        }

        return null;
    }

    public ArrayList<Stock> findAll() {
        return stocks;
    }


    public ArrayList<Stock> findByColumnAndValue(ArrayList<StockField> stockFields)
    {
        ArrayList<Stock> matchedStocks = new ArrayList<>(stocks);
        ArrayList<Stock> matchingStocks = new ArrayList<>();
        boolean done = false;

        for (StockField field : stockFields)
        {
            int i = 0;
            for (Stock stock : matchedStocks)
            {
                i++;
                if (findMatch(stock, field))
                {
                    matchingStocks.add(stock);
                }
            }

            matchedStocks.clear();
            matchedStocks.addAll(matchingStocks);
            matchingStocks.clear();
        }

        return matchedStocks;
    }

    private boolean findMatch(Stock stock, StockField field)
    {
        boolean match = false;

        if (field.getName() == (StockFieldType.SYMBOL))
        {
            StockStringField stringField = (StockStringField) field;

            if ((stringField.contains(stock.getSymbol())))
            {
                match = true;
            }
        }
        else if (field.getName() == (StockFieldType.NAME))
        {
            StockStringField stringField = (StockStringField) field;

            if ((stringField.contains(stock.getName())))
            {
                match = true;
            }
        }
        else if (field.getName() == (StockFieldType.PRICE))
        {
            StockDecimalField numberField = (StockDecimalField) field;

            if ((numberField.compareValue(stock.getPrice())))
            {
                match = true;
            }
        }
        else if (field.getName() == (StockFieldType.DIVIDEND))
        {
            StockDecimalField numberField = (StockDecimalField) field;

            if ((numberField.compareValue(stock.getDividend())))
            {
                match = true;
            }
        }
        else if (field.getName() == (StockFieldType.YIELD))
        {
            StockDecimalField numberField = (StockDecimalField) field;

            if ((numberField.compareValue(stock.getYield())))
            {
                match = true;
            }
        }
        else if (field.getName() == (StockFieldType.DATE))
        {
            StockDateField dateField = (StockDateField) field;

            if ((dateField.compareDate(stock.getLastDividendDate())))
            {
                match = true;
            }
        }
        else
        {
            match = false;
        }

        return match;
    }

    public Stock getData(String symbol)
    {
        return StockDataImporter.refreshStockData(symbol);
    }

    private static String getFieldByType(StockFieldType type) {
        switch(type) {
            case SYMBOL:
                return "String";
            case NAME:
                return "String";
            case PRICE:
                return "Double";
            case DIVIDEND:
                return "Double";
            case YIELD:
                return "Double";
            case DATE:
                return "Date";
        }

        throw new IllegalArgumentException("Cannot get field of type " + type);
    }
}
