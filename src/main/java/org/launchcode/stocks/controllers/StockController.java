package org.launchcode.stocks.controllers;

import org.launchcode.stocks.models.*;
import org.launchcode.stocks.models.data.StockData;
import org.launchcode.stocks.models.forms.SearchForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;

@Controller
@RequestMapping("stock")
public class StockController {

    StockData stockData = StockData.getInstance();

    @RequestMapping(value = "")  //set Route for home page
    public String index(Model model)
    {
        String title ="My Stocks";

        HashMap<String, String> stockMap = stockData.getSymbolsAndNames();
        ArrayList<String> symbols = new ArrayList<>(stockData.getSymbolsAndNames().keySet());
        Collections.sort(symbols);

        model.addAttribute("title", title);
        model.addAttribute("stocks", stockMap);
        model.addAttribute("symbols", symbols);

        return "stock/index";
    }

    @RequestMapping(value = "detail", method = RequestMethod.GET)  //set Route for home page
    public String detail(Model model, @RequestParam String symbol,
                         @CookieValue(value = "user", defaultValue = "none") String username)
    {
        boolean loggedIn = true;
        if(username.equals("none")) {
            loggedIn = false;
        }

        String title ="Stock";
        model.addAttribute("title", title);

        Stock stock = stockData.findBySymbol(symbol);
        if(stock == null)
        {
            System.out.println("\n invalid: " + symbol);
        }
        double diff = (stock.getPrice() - stock.getWeekStartPrice());
        diff = Math.floor((diff*100.0)+0.5)/100.00;
        model.addAttribute("stock", stock);
        model.addAttribute("user", loggedIn);
        model.addAttribute("diff", diff);
        return "stock/detail";
    }

    @RequestMapping(value = "search")
    public String displaySearchForm(Model model)  {

        StockCompareType[] types = StockCompareType.values();
        model.addAttribute("types", types);
        model.addAttribute(new SearchForm());
        model.addAttribute("title", "Search for Stocks");

        return "stock/search";
    }

    @RequestMapping(value = "search/results", method = RequestMethod.POST)
    public String processSearchForm(@ModelAttribute @Valid SearchForm newsearch,
                                    Errors errors, Model model,
                                    @CookieValue(value = "user", defaultValue = "none") String username)
    {
        if (errors.hasErrors()) {
            StockCompareType[] types = StockCompareType.values();
            model.addAttribute("types", types);
            model.addAttribute("title", "Search for Stocks");

            return "stock/search";
        }

        boolean loggedIn = true;
        if(username.equals("none")) {
            loggedIn = false;
        }

        ArrayList<StockField> fields = new ArrayList<>();

        if (!(newsearch.getSymbol().equals("")))
        {
            StockStringField field = new StockStringField(StockFieldType.SYMBOL, newsearch.getSymbol());
            fields.add(field);
        }
        if (!(newsearch.getName().equals("")))
        {
            StockStringField field = new StockStringField(StockFieldType.NAME, newsearch.getName());
            fields.add(field);
        }
        if (((newsearch.getPrice() > 0)) && (newsearch.getPriceComp() != StockCompareType.NONE))
        {
            StockDecimalField field = new StockDecimalField(StockFieldType.PRICE, newsearch.getPriceComp(),
                    newsearch.getPrice());
            fields.add(field);
        }
        if (((newsearch.getDividend() > 0)) && (newsearch.getDivComp() != StockCompareType.NONE))
        {
            StockDecimalField field = new StockDecimalField(StockFieldType.DIVIDEND, newsearch.getDivComp(),
                    newsearch.getDividend());
            fields.add(field);
        }
        if (((newsearch.getYield() > 0)) && (newsearch.getYieldComp() != StockCompareType.NONE))
        {
            StockDecimalField field = new StockDecimalField(StockFieldType.YIELD, newsearch.getYieldComp(),
                    newsearch.getYield());
            fields.add(field);
        }
        if (((newsearch.getYear() > 0)) && ((newsearch.getMonth() > 0)) && ((newsearch.getDay() > 0)) &&
                (newsearch.getDateComp() != StockCompareType.NONE))
        {
            GregorianCalendar date = new GregorianCalendar(newsearch.getYear(),
                    newsearch.getMonth()-1, newsearch.getDay());
            StockDateField field = new StockDateField(StockFieldType.DATE, newsearch.getDateComp(), date);
            fields.add(field);
        }

        ArrayList<Stock> foundStocks = stockData.findByColumnAndValue(fields);

        if (foundStocks.isEmpty())
        {
            if ((!(newsearch.getSymbol().equals(""))) &&
                    (stockData.getSymbolsAndNames().containsKey(newsearch.getSymbol())))
            {
                String symbol = newsearch.getSymbol();
                Stock stock = stockData.findBySymbol(symbol);
                model.addAttribute("stock", stock);

                double diff = (stock.getPrice() - stock.getWeekStartPrice());
                diff = Math.floor((diff*100.0)+0.5)/100.00;
                model.addAttribute("diff", diff);
                model.addAttribute("user", loggedIn);

                return "stock/detail";
            }
            else if ((!(newsearch.getName().equals(""))) &&
                    (stockData.getSymbolsAndNames().values().contains(newsearch.getName())))
            {
                String name = newsearch.getName();
                Stock stock = stockData.findByName(name);
                model.addAttribute("stock", stock);

                double diff = (stock.getPrice() - stock.getWeekStartPrice());
                diff = Math.floor((diff*100.0)+0.5)/100.00;
                model.addAttribute("diff", diff);
                model.addAttribute("user", loggedIn);

                return "stock/detail";
            }
            else
            {
                HashMap<String, String> stockMap = stockData.getSymbolsAndNames();
                ArrayList<String> symbols = new ArrayList<>(stockData.getSymbolsAndNames().keySet());
                Collections.sort(symbols);

                model.addAttribute("stocks", stockMap);
                model.addAttribute("title", foundStocks.size() + " Stocks found");
                model.addAttribute("symbols", symbols);

                return "stock/index";
            }
        }
        else
        {
            HashMap<String, String> stockMap = new HashMap<>();

            for (int i=0; i < foundStocks.size(); i++)
            {
                stockMap.put(foundStocks.get(i).getSymbol(), foundStocks.get(i).getName());
            }

            ArrayList<String> symbols = new ArrayList<>(stockMap.keySet());
            Collections.sort(symbols);

            String title ="Search Results";

            model.addAttribute("stocks", stockMap);
            model.addAttribute("title", title);
            model.addAttribute("symbols", symbols);

            return "stock/results";
        }
    }
}