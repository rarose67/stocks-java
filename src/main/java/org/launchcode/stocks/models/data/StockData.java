package org.launchcode.stocks.models.data;

import org.launchcode.stocks.models.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class provides methods and structures to access the data retrieved from the IEX API.
 */
public class StockData {

    private ArrayList<Stock> stocks = new ArrayList<>();
    private static HashMap<String, String> offeredStocks = new HashMap<>();
    private static HashMap<String, String> allStocks = new HashMap<>();
    private static StockData instance;

    /**
     * The function takes a double and shits the decimal point a given number of places.
     * @param num - The number to be shifted
     * @param places - the number of places to shit the decimal point.  A positive value shits to the right,
     *               A negative value shits to the left.
     * @return - the number after the shift
     */
    public static double decimalPlaces(double num, int places)
    {
        double multiplier = Math.pow(10.0, places);
        double dec =  Math.floor((num * multiplier) + 0.5) / multiplier;

        return dec;
    }

    /**
     * Returns a hashmap which relates a stock's symbol to it's name.  This hashmap only contains entries for
     * stocks which have dividends.
     * @return
     */
    public HashMap<String, String> getSymbolsAndNames() {
        return offeredStocks;
    }

    /**
     * Returns a hashmap which relates a stock's symbol to it's name.  This hashmap contains entries for
     * all available stocks.
     * @return
     */
    public HashMap<String, String> getAllSymbolsAndNames() {
        return allStocks;
    }

    private StockData() {
        StockDataImporter.loadSymbols(this);

    }

    /**
     * Creates an instance of the Stockdata class if one doesn't already exsit.
     * @return - A reference to the instance
     */
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

    /**
     * Finds a stock with a given symbol
     * @param symbol - The stock symbol to search for.
     * @return - A reference to the stock object or null
     */
    public Stock findBySymbol(String symbol) {
        for (Stock stock : stocks) {
            if (stock.getSymbol().equals(symbol))
                return stock;
        }

        return null;
    }

    /**
     * Finds a stock with a given name
     * @param name - The stock name to search for.
     * @return - A reference to the stock object or null
     */
    public Stock findByName(String name) {
        for (Stock stock : stocks) {
            if (stock.getName().equals(name))
                return stock;
        }

        return null;
    }

    /**
     * Find All stocks.
     * @return - A List of Stock objects.
     */
    public ArrayList<Stock> findAll() {
        return stocks;
    }


    /**
     * Find all stocks that match given criteria.
     * @param stockFields - A List of StockField objects that contain the field names and values to search for.
     * @return - A list of stock objects that match given criteria.
     */
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

    /**
     * This function determines whether a stock matches a search criteria.
     * @param stock - The stock to check
     * @param field - The criteria to check against
     * @return - whether there's a match
     */
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

    /**
     * Refresh the data for a stock from the IEX API.
     * @param symbol The symbol of the stock
     * @return - A reference to the updated stock object
     */
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
