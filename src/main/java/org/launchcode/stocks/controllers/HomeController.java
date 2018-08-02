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

    public void loadSimPositions(StockData stockData)
    {
         SimStockData simStockData = SimStockData.getInstance();

        if (simStockData.findAll().isEmpty()) {
            List<String> symbols = positionDao.findSymbols();
            Stock stock;

            for (String symbol : symbols) {
                stock = stockData.findBySymbol(symbol);
                simStockData.add(stock);
            }
        }
    }

    public void loadSimPositions()
    {
        StockData stockData = StockData.getInstance();

        SimStockData simStockData = SimStockData.getInstance();

        if (simStockData.findAll().isEmpty()) {
            List<String> symbols = positionDao.findSymbols();
            Stock stock;

            for (String symbol : symbols) {
                stock = stockData.findBySymbol(symbol);
                simStockData.add(stock);
            }
        }
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index()
    {
        loadSimPositions(stockData);
        return "home";
    }
}
