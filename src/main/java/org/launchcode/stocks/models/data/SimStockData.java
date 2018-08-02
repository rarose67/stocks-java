package org.launchcode.stocks.models.data;

import org.launchcode.stocks.models.*;

import java.util.ArrayList;
import java.util.HashMap;

public class SimStockData {

    private ArrayList<SimStock> simStocks = new ArrayList<>();
    private static HashMap<String, String> symbolsAndNames = new HashMap<>();
    private static SimStockData instance;

    public HashMap<String, String> getSymbolsAndNames() {
        return symbolsAndNames;
    }

    private SimStockData() {

    }

    public static SimStockData getInstance() {
        if (instance == null) {
            instance = new SimStockData();
        }
        return instance;
    }

    public SimStock add(Stock stock)
    {
        if (!(symbolsAndNames.containsKey(stock.getSymbol()))) {
            SimStock simStock = new SimStock(stock);
            simStocks.add(simStock);
            symbolsAndNames.put(simStock.getSymbol(), simStock.getName());

            return simStock;
        }
        else
        {
            return findBySymbol(stock.getSymbol());
        }

    }

    public SimStock findById(int id) {
        for (SimStock stock : simStocks) {
            if (stock.getId() == id)
                return stock;
        }

        return null;
    }

    public SimStock findBySymbol(String symbol) {
        for (SimStock stock : simStocks) {
            if (stock.getSymbol().equals(symbol))
                return stock;
        }

        return null;
    }

    public SimStock findByName(String name) {
        for (SimStock stock : simStocks) {
            if (stock.getName().equals(name))
                return stock;
        }

        return null;
    }

    public ArrayList<SimStock> findAll() {
        return simStocks;
    }


    public ArrayList<SimStock> findByColumnAndValue(ArrayList<StockField> stockFields)
    {
        ArrayList<SimStock> matchedStocks = new ArrayList<>(simStocks);
        ArrayList<SimStock> matchingStocks = new ArrayList<>();
        boolean done = false;

        for (StockField field : stockFields)
        {
            int i = 0;
            for (SimStock stock : matchedStocks)
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

    private boolean findMatch(SimStock stock, StockField field)
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

            if ((dateField.compareDate(stock.getNextDividendDate())))
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


