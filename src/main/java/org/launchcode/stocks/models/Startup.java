package org.launchcode.stocks.models;

import org.launchcode.stocks.models.data.PositionDao;
import org.launchcode.stocks.models.data.SimStockData;
import org.launchcode.stocks.models.data.StockData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Startup {

   @Autowired
   private PositionDao positionDao;

    private StockData stockData = StockData.getInstance();

    public Startup()
    {
    }

    public void loadSimPositions()
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

    public void reloadData()
    {
        stockData.reload();
    }
}
