package org.launchcode.stocks.controllers;

import org.launchcode.stocks.models.SimStock;
import org.launchcode.stocks.models.Stock;
import org.launchcode.stocks.models.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController
{
    @Autowired
    private PositionDao positionDao;

    private StockData stockData = StockData.getInstance();

    private SimStockData simStockData = SimStockData.getInstance();

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index()
    {
        if (simStockData.findAll().isEmpty())
        {
            List<String> symbols = positionDao.findSymbols();
            Stock stock;

            for (String symbol : symbols)
            {
                stock = stockData.findBySymbol(symbol);
                simStockData.add(stock);
            }
        }

        return "home";
    }
}
