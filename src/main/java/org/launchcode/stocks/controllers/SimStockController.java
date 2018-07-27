package org.launchcode.stocks.controllers;

import org.launchcode.stocks.models.*;
import org.launchcode.stocks.models.data.SimStockData;
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
@RequestMapping("simstock")
public class SimStockController {

    StockData stockData = StockData.getInstance();
    SimStockData simStockData = SimStockData.getInstance();

    @RequestMapping(value = "")  //set Route for home page
    public String index(Model model)
    {
        String title ="My simulated Stocks";

        //HashMap<String, String> stockMap = simStockData.getSymbolsAndNames();
        HashMap<String, String> simStockMap = new HashMap<>();

        for (int i=0; i < simStockData.findAll().size(); i++)
        {
            simStockMap.put(simStockData.findAll().get(i).getSymbol(), simStockData.findAll().get(i).getName());
        }

        ArrayList<String> symbols = new ArrayList<>(simStockMap.keySet());
        Collections.sort(symbols);

        model.addAttribute("title", title);
        model.addAttribute("simStocks", simStockMap);
        model.addAttribute("symbols", symbols);

        return "simstock/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddForm(Model model, @RequestParam String symbol)
    {
        String title ="Simulated Stock";
        model.addAttribute("title", title);

        Stock stock = stockData.findBySymbol(symbol);
        SimStock simStock = simStockData.add(stock);
        double diff = (simStock.getPrice() - simStock.getWeekStartPrice());
        diff = SimStock.decimalPlaces(diff,2);
        model.addAttribute("simStock", simStock);
        model.addAttribute("diff", diff);
        return "redirect:/simstock/detail/" + simStock.getSymbol();
    }

    @RequestMapping(value = "detail/{symbol}", method = RequestMethod.GET)  //set Route for home page
    public String detail(Model model, @PathVariable String symbol)
    {
        String title ="Simulated Stock";
        model.addAttribute("title", title);

        SimStock simStock = simStockData.findBySymbol(symbol);
        double diff = (simStock.getPrice() - simStock.getWeekStartPrice());
        diff = SimStock.decimalPlaces(diff,2);
        model.addAttribute("simStock", simStock);
        model.addAttribute("diff", diff);
        return "simstock/detail";
    }

    @RequestMapping(value = "search")
    public String displaySearchForm(Model model)  {

        StockCompareType[] types = StockCompareType.values();
        model.addAttribute("types", types);
        model.addAttribute(new SearchForm());
        model.addAttribute("title", "Search for simulated Stocks");

        return "search";
    }

    @RequestMapping(value = "search/results", method = RequestMethod.POST)
    public String processSearchForm(@ModelAttribute @Valid SearchForm newsearch,
                                    Errors errors, Model model)
    {
        if (errors.hasErrors()) {
            StockCompareType[] types = StockCompareType.values();
            model.addAttribute("types", types);
            model.addAttribute("title", "Search for simulated Stocks");

            return "search";
        }

        ArrayList<StockField> fields = new ArrayList<>();

        if (!(newsearch.getSymbol().equals("")))
        {
            StockStringField field = new StockStringField(StockFieldType.SYMBOL, newsearch.getName());
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

        ArrayList<SimStock> foundStocks = simStockData.findByColumnAndValue(fields);

        if (foundStocks.isEmpty())
        {
            if ((!(newsearch.getSymbol().equals(""))) &&
                    (simStockData.getSymbolsAndNames().containsKey(newsearch.getSymbol())))
            {
                String symbol = newsearch.getSymbol();
                SimStock simStock = simStockData.findBySymbol(symbol);
                model.addAttribute("simStock", simStock);

                double diff = (simStock.getPrice() - simStock.getWeekStartPrice());
                diff = Math.floor((diff*100.0)+0.5)/100.00;
                model.addAttribute("diff", diff);

                return "simstock/detail";
            }
            else if ((!(newsearch.getName().equals(""))) &&
                    (simStockData.getSymbolsAndNames().values().contains(newsearch.getName())))
            {
                String name = newsearch.getName();
                SimStock simStock = simStockData.findByName(name);
                model.addAttribute("simStock", simStock);

                double diff = (simStock.getPrice() - simStock.getWeekStartPrice());
                diff = Math.floor((diff*100.0)+0.5)/100.00;
                model.addAttribute("diff", diff);

                return "simstock/detail";
            }
            else
            {
                HashMap<String, String> simStockMap = new HashMap<>();

                for (int i=0; i < simStockData.findAll().size(); i++)
                {
                    simStockMap.put(simStockData.findAll().get(i).getSymbol(),
                            simStockData.findAll().get(i).getName());
                }

                ArrayList<String> symbols = new ArrayList<>(simStockMap.keySet());
                Collections.sort(symbols);

                model.addAttribute("simStocks", simStockMap);
                model.addAttribute("title", foundStocks.size() +
                        "Simulated Stocks found");
                model.addAttribute("symbols", symbols);

                return "simstock/index";
            }
        }
        else
        {
            HashMap<String, String> simStockMap = new HashMap<>();

            for (int i=0; i < foundStocks.size(); i++)
            {
                simStockMap.put(foundStocks.get(i).getSymbol(), foundStocks.get(i).getName());
            }

            ArrayList<String> symbols = new ArrayList<>(simStockMap.keySet());
            Collections.sort(symbols);

            String title ="Search Results";

            model.addAttribute("simStocks", simStockMap);
            model.addAttribute("title", title);
            model.addAttribute("symbols", symbols);

            return "simstock/index";
        }
    }
}
