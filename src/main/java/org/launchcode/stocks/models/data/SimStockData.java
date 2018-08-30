package org.launchcode.stocks.models.data;

import org.launchcode.stocks.models.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class provides methods and structures to access the simulated stocks used for projection calculation.
 */
public class SimStockData {

    private ArrayList<SimStock> simStocks = new ArrayList<>();
    private static HashMap<String, String> symbolsAndNames = new HashMap<>();
    private static SimStockData instance;

    /**
     * Returns a hashmap which relates a simulated stock's symbol to it's name.
     * @return
     */
    public HashMap<String, String> getSymbolsAndNames() {
        return symbolsAndNames;
    }

    private SimStockData() {

    }

    /**
     * Creates an instance of the SimStockdata class if one doesn't already exsit.
     * @return - A reference to the instance
     */
    public static SimStockData getInstance() {
        if (instance == null) {
            instance = new SimStockData();
        }
        return instance;
    }

    /**
     * Add a SimStock to the list of stocks
     * @param stock - The SimStock to add
     * @return - A reference to the SimStock
     */
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

    /**
     * Find a SimStock by id
     * @param id - The id of the SimStock
     * @return - A reference to the SimStock or null
     */
    public SimStock findById(int id) {
        for (SimStock stock : simStocks) {
            if (stock.getId() == id)
                return stock;
        }

        return null;
    }

    /**
     * Find a SimStock by stock symbol
     * @param symbol - The symbol of the SimStock
     * @return - A reference to the SimStock or null
     */
    public SimStock findBySymbol(String symbol) {
        for (SimStock stock : simStocks) {
            if (stock.getSymbol().equals(symbol))
                return stock;
        }

        return null;
    }

    /**
     * Find a SimStock by name
     * @param name - The name of the SimStock
     * @return - A reference to the SimStock or null
     */
    public SimStock findByName(String name) {
        for (SimStock stock : simStocks) {
            if (stock.getName().equals(name))
                return stock;
        }

        return null;
    }

    /**
     * Find All SimStocks.
     * @return - A List of SimStock objects.
     */
    public ArrayList<SimStock> findAll() {
        return simStocks;
    }

    /**
     * Find all SimStocks that match given criteria.
     * @param stockFields - A List of StockField objects that contain the field names and values to search for.
     * @return - A list of Simstock objects that match given criteria.
     */
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

    /**
     * This function determines whether a SimStock matches a search criteria.
     * @param stock - The stock to check
     * @param field - The criteria to check against
     * @return - whether there's a match
     */
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


