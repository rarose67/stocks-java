package org.launchcode.stocks.models.data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.launchcode.stocks.models.Stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class StockDataImporter {

    private static HashMap<String, Stock> stockList = new HashMap<>();
    private static boolean isLoaded = false;

    /**
     * This function connects to a web address an returns the response.
     *
     * * The code was derived from the following youtube video:
     *      *
     *      * JAVA-Send URL HTTP Request and Read JSON Response
     *      * https://www.youtube.com/watch?v=umZ_KdcXRAQ
     *
     * @param address - The address to connect to.
     * @return - The JSON response from the webpage.
     */
    private static String apiConnect(String address)
    {
        URL url = null;
        try {
            url = new URL(address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }

            in.close();

            return response.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Numeric values given  by the IEX api can be different types based on thier value:
     * Values less or greater than 0 are of type Double.
     * Values equal to 0 are of type Integer.
     * Null values are empty strings.
     *
     * This function convents the value in a given JSON object field to a Double.
     *
     * @param jsonObject - the JSON object that contains the value.
     * @param field - The key of the field in the JSON object.
     * @return The value as a Double.  If the value is null, -999999.99 is returned.
     */
    private static double getDouble(JSONObject jsonObject, String field)
    {
        String stringExsist;
        int intExsist;
        Number number;
        double doubleExsist, value;

        stringExsist = jsonObject.optString(field, "none");
        intExsist = jsonObject.optInt(field, -1);
        doubleExsist = jsonObject.optDouble(field, -1.0);

        if (!(doubleExsist == -1.0))
        {
            value = jsonObject.getDouble(field);
            return value;
        }
        else if (!(intExsist == -1))
        {
            number = (Number) jsonObject.get(field);
            value = number.doubleValue();
            return value;
        }
        else
        {
            return -1.0;
        }
    }

    /**
     * This function loads the stock data from the IEX API.
     *
     * @param stockData - the instance a the stockdata class
     */
    public static void loadSymbols(StockData stockData)
    {
        if (isLoaded)
        {
            return;
        }

        String address = "https://api.iextrading.com/1.0/ref-data/symbols";
        String response = apiConnect(address);
        JSONArray myResponse = new JSONArray(response);
        JSONObject obj;
        String symbol, name;
        ArrayList<String> symbols = new ArrayList<>();

        for (int i=0; i < myResponse.length(); i++)
        {
            obj = myResponse.getJSONObject(i);
            symbol = obj.getString("symbol");
            name = obj.getString("name");
            stockData.getSymbolsAndNames().put(symbol, name);
            symbols.add(symbol);

            if ((i+1 >= myResponse.length()) || (((i+1) % 100) == 0))
            {
                symbols = removeZeroDividends(symbols, stockData);
                loadStockData(symbols);
                symbols.clear();
            }
        }

        for (String key : stockData.getSymbolsAndNames().keySet())
        {

            stockData.findAll().add(stockList.get(key));
        }

        isLoaded = true;
    }

    /**
     * This function takes a list of stock symbols and determines whether those stocks have dividends.
     * If a stock gives a dividend, a stock object is created, it's last dividend is calculated via
     * the ex-dividend date, and the object is added to the static stocksList Hashmap.
     *
     * Otherwise, the symbol is removed from the StockSymbols Hashmap.
     *
     * @param symbols - The list of symbols to be checked
     * @param stockData - the instance a the stockdata class
     * @return validSymbols - An ArrayList of stock symbols with valid dividend.
     */
    private static ArrayList<String> removeZeroDividends(ArrayList<String> symbols, StockData stockData)  {

        Number dividendRate;
        String dividendRateExist, symbolString = "";
        double dividend;
        JSONObject myObject, mySubObject;
        ArrayList<String> validSymbols = new ArrayList<>();

        for (int i=0; i<symbols.size(); i++)
        {
            if (!(symbols.get(i).endsWith("#")))
            {
                symbolString += symbols.get(i);
                if (i < (symbols.size()-1))
                {
                    symbolString += ",";
                }
            }

        }

        String address = "https://api.iextrading.com/1.0/stock/market/batch?symbols=" + symbolString + "&types=stats";
        String response = apiConnect(address);
        //System.out.println("\n" + address);

        myObject = new JSONObject(response);

        for (int i=0; i<symbols.size(); i++)
        {
            String symbol = symbols.get(i);
            if (!(myObject.isNull(symbol)))
            {
                mySubObject = myObject.optJSONObject(symbols.get(i)).getJSONObject("stats");

                dividendRateExist = mySubObject.optString("dividendRate", "No dividend");
                if (dividendRateExist.equals("No dividend"))
                {
                    stockData.getSymbolsAndNames().remove(symbols.get(i));
                }
                else
                {
                    dividendRate = (Number) mySubObject.get("dividendRate");
                    dividend = dividendRate.doubleValue();

                    if (dividend <= 0.0)
                    {
                        stockData.getSymbolsAndNames().remove(symbols.get(i));
                    }
                    else
                    {

                        String name = stockData.getSymbolsAndNames().get(symbol);
                        Stock stock = new Stock(symbol, name, dividend);
                        stock.setLastDividendDate(loadExDate(mySubObject));

                        stockList.put(symbol, stock);
                        validSymbols.add(symbol);
                    }
                }
            }
            else
            {
                stockData.getSymbolsAndNames().remove(symbols.get(i));
            }
        }

        return validSymbols;
    }

    /**
     * This funcltion calculates the last dividend date based on the ex-dividend date.
     *
     * @param stats the JSON object containing the ex-dividend date.
     * @return
     */
    private static GregorianCalendar loadExDate(JSONObject stats)
    {
        String field = stats.getString("exDividendDate");
        String[] dateTime = field.split(" ");
        String[] dateParts = dateTime[0].split("-");
        String yearString = dateParts[0];
        String monthString = dateParts[1];
        String dayString = dateParts[2];
        int year = Integer.parseInt(yearString);
        int month = Integer.parseInt(monthString);
        int day = Integer.parseInt(dayString);
        GregorianCalendar divDate = new GregorianCalendar(year, (month-1), day);
        divDate.add(GregorianCalendar.DATE,14);
        return divDate;
    }

/**  public static void printStockNewData(String symbol)
 {
 String address = "https://api.iextrading.com/1.0/stock/" + symbol + "/stats" +
 "?filter=dividendRate,month1ChangePercent";
 String response = apiConnect(address);

 JSONObject myObject = new JSONObject(response);
 Number dividendRate = (Number) myObject.get("dividendRate");
 double dividend = dividendRate.doubleValue();

 if (dividend == 0) {
 System.out.println("No Dividend");
 }

 double month1ChangePercent = (Double) myObject.get("month1ChangePercent");

 address = "https://api.iextrading.com/1.0/stock/" + symbol + "/previous";
 response = apiConnect(address);

 myObject = new JSONObject(response);

 double price = (Double) myObject.get("close");
 double variance = Math.abs(price * month1ChangePercent);

 GregorianCalendar date = loadDate(symbol);
 double weekStartPrice= weekStartPrice(symbol);

 System.out.println("symbol: " + symbol + "\nprice: " + price + "\nvariance: " +
 variance + "\ndividend: " + dividend +
 "\nLastdivDate: " + (date.get(GregorianCalendar.MONTH)+1)  + "/" + date.get(GregorianCalendar.DAY_OF_MONTH) + "/" + date.get(GregorianCalendar.YEAR) +
 "\nStart of week Price: " + weekStartPrice);

 } */

    /**
     * Reload the data for the stock with a given symbol.
     * @param symbol - Symbol of the stock.
     * @return a reference to the stock object.
     */
    public static Stock refreshStockData(String symbol)
    {
        ArrayList<String> singleItemList = new ArrayList<>();

        singleItemList.add(symbol);
        loadStockData(singleItemList);

        return stockList.get(symbol);

    }

    /**
     * Loads the stock data for the stocks with the symbols in the list.
     * @param symbols - list of stock symbols
     */
    private static void loadStockData(ArrayList<String> symbols)
    {
        String symbolString = "";

        for (int i=0; i<symbols.size(); i++)
        {
            symbolString += symbols.get(i);
            if (i < (symbols.size()-1))
            {
                symbolString += ",";
            }
        }

        loadDividendInfo(symbols, symbolString);
        weekStartPrice(symbols, symbolString);
        loadStats(symbols, symbolString);
    }

    /**
     * load the price of the stock at the start of the week into stock objects.
     * @param symbols -  list of stock symbols
     * @param symbolString - String of symbols to be used in the batch request.
     */
    private static void weekStartPrice(ArrayList<String> symbols, String symbolString)
    {
        JSONObject myObject, mySubObject;
        JSONArray myArray;
        String openExist;
        Stock stock;
        double open, close;

        GregorianCalendar startofWeek = (GregorianCalendar) GregorianCalendar.getInstance();
        int dtm = Math.abs(GregorianCalendar.MONDAY - startofWeek.get(GregorianCalendar.DAY_OF_WEEK));
        dtm = 5 - dtm;
        if ((dtm < 0) || (dtm >= 5))
        {
            dtm = 0;
        }

        String address = "https://api.iextrading.com/1.0/stock/market/batch?symbols=" + symbolString +
                "&types=chart&range=5d&filter=date,open,close";
        String response = apiConnect(address);

        myObject = new JSONObject(response);

        for (int i=0; i<symbols.size(); i++) {
            myArray = myObject.optJSONObject(symbols.get(i)).optJSONArray("chart");

            mySubObject = myArray.optJSONObject(dtm);

            while ((mySubObject == null) && (dtm > 0))
            {
                dtm--;
               // System.out.println("\n" + symbols.get(i));
                mySubObject = myArray.optJSONObject(dtm);
            }

            stock = stockList.get(symbols.get(i));
            openExist = mySubObject.optString("open", "");
            if (!(openExist.equals("")))
            {
                open = ((Number) mySubObject.get("open")).doubleValue();
                stock.setWeekStartPrice(open);
            }
            else
            {
                close = ((Number) mySubObject.get("close")).doubleValue();
                stock.setWeekStartPrice(close);
            }
        }
    }

    /**
     * load the last dividend date and amount of the stock into stock objects.
     * @param symbols -  list of stock symbols
     * @param symbolString - String of symbols to be used in the batch request.
     */
    private static void loadDividendInfo(ArrayList<String> symbols, String symbolString)
    {
        String divDateString, yearString, monthString, dayString, flag;
        String[] dateParts;
        int year, month, day;
        GregorianCalendar divDate;

        double dividend;

        JSONObject myObject, mySubObject;
        JSONArray myArray;
        Stock stock;
        int j;
        boolean found = false;

        String address = "https://api.iextrading.com/1.0/stock/market/batch?symbols=" + symbolString +
                "&types=dividends&range=1y&filter=paymentDate,amount,flag";
        String response = apiConnect(address);

        myObject = new JSONObject(response);

        for (int i=0; i<symbols.size(); i++) {
            myArray = myObject.optJSONObject(symbols.get(i)).optJSONArray("dividends");

            found = false;
            j=0;
            while (j < myArray.length() && !(found)) {

                mySubObject = myArray.getJSONObject(j);
                flag = mySubObject.getString("flag");
                divDateString = mySubObject.getString("paymentDate");

                if ((flag.equals("") || flag.equals("FI")) && !(divDateString.equals("")))
                {
                    dateParts = divDateString.split("-");
                    yearString = dateParts[0];
                    monthString = dateParts[1];
                    dayString = dateParts[2];
                    year = Integer.parseInt(yearString);
                    month = Integer.parseInt(monthString);
                    day = Integer.parseInt(dayString);
                    divDate = new GregorianCalendar(year, (month - 1), day);

                    stock = stockList.get(symbols.get(i));
                    stock.setLastDividendDate(divDate);

                    dividend = getDouble(mySubObject, "amount");
                    if(dividend > 0.0)
                    {
                        stock.setqDividend(dividend);
                        found = true;
                    }
                }

                j++;
            }
        }
    }

    /**
     * load the price, variance, and yield of the stock into stock objects.
     * @param symbols -  list of stock symbols
     * @param symbolString - String of symbols to be used in the batch request.
     */
    private static void loadStats(ArrayList<String> symbols, String symbolString)
    {
        JSONObject myObject, mySubObject;
        JSONObject myObject2, mySubObject2;
        Stock stock;
        String name;
        double month1ChangePercent, price, variance, yield;

        String address = "https://api.iextrading.com/1.0/stock/market/batch?symbols=" + symbolString +
                "&types=stats&filter=companyName,month1ChangePercent";
        String response = apiConnect(address);

        String address2 = "https://api.iextrading.com/1.0/stock/market/batch?symbols=" + symbolString +
                "&types=previous&filter=close";
        String response2 = apiConnect(address2);

        myObject = new JSONObject(response);
        myObject2 = new JSONObject(response2);

        for (int i=0; i<symbols.size(); i++)
        {
            mySubObject = myObject.optJSONObject(symbols.get(i)).optJSONObject("stats");
            mySubObject2 = myObject2.optJSONObject(symbols.get(i)).optJSONObject("previous");

            name = mySubObject.getString("companyName");
            month1ChangePercent = ((Number) mySubObject.get("month1ChangePercent")).doubleValue();
            price = ((Number) mySubObject2.get("close")).doubleValue();
            variance = Math.abs(price * month1ChangePercent);

            stock = stockList.get(symbols.get(i));
            stock.setPrice(price);
            stock.setName(name);
            stock.setVariance(variance);
            stock.setYield();
        }
    }
}
