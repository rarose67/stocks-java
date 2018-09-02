package org.launchcode.stocks.controllers;

import org.launchcode.stocks.models.Position;
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

    /**
     *
     * @param stockData
     */
    public void loadSimPositions(StockData stockData)
    {
         SimStockData simStockData = SimStockData.getInstance();

        if (simStockData.findAll().isEmpty()) {
            List<String> symbols = positionDao.findSymbols();
            Stock stock;

            for (String symbol : symbols) {
                stock = stockData.findBySymbol(symbol);
                if (stock != null) {
                    simStockData.add(stock);
                }
                else
                {
                    List<Position> oldPositionList = positionDao.findBySymbol(symbol);

                    for (Position position : oldPositionList)
                    {
                        position.setValid(false);
                        positionDao.save(position);
                    }
                }
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
                if (stock != null) {
                    simStockData.add(stock);
                }
                else
                {
                    List<Position> oldPositionList = positionDao.findBySymbol(symbol);

                    for (Position position : oldPositionList)
                    {
                        positionDao.delete(position.getId());
                    }
                }
            }
        }
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index()
    {
        //loadSimPositions(stockData);
        return "home";
    }
}
