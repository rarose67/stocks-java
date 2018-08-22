package org.launchcode.stocks.controllers;

import org.launchcode.stocks.models.*;
import org.launchcode.stocks.models.data.*;
import org.launchcode.stocks.models.forms.PositionForm;
import org.launchcode.stocks.models.forms.SearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("position")
public class PositionController {

    @Autowired
    private PortfolioDao portfolioDao;

    @Autowired
    private PositionDao positionDao;

    @Autowired
    private UserDao userDao;

    private StockData stockData = StockData.getInstance();

    private SimStockData simStockData = SimStockData.getInstance();

    public void loadSimPositions(StockData stockData)
    {
        if (simStockData.findAll().isEmpty()) {
            List<String> symbols = positionDao.findSymbols();
            Stock stock;

            for (String symbol : symbols) {
                stock = stockData.findBySymbol(symbol);
                if (stock != null) {
                    simStockData.add(stock);
                }
            }
        }
    }

    private void deletePosition(Position oldPosition)
    {
        if (oldPosition.getState() == PositionState.ACTIVE)
        {
            oldPosition.setState(PositionState.INACTIVE);
            positionDao.save(oldPosition);
        }
        else if (oldPosition.getState() == PositionState.INACTIVE)
        {
            oldPosition.setState(PositionState.DELETED);
            positionDao.save(oldPosition);
        }
        else if (oldPosition.getState() == PositionState.DELETED) {
            Portfolio portfolio = oldPosition.getPortfolio();
            portfolio.removeItem(oldPosition);
            positionDao.delete(oldPosition.getId());
        }
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddPositionForm(Model model, @RequestParam String symbol,
                                         @CookieValue(value = "portfolio", defaultValue = "-1") String portfolioId,
                                         @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        if(portfolioId.equals("-1")) {
            return "redirect:/portfolio";
        }

        int pId = Integer.parseInt(portfolioId);
        Portfolio p = portfolioDao.findOne(pId);

        loadSimPositions(stockData);
        SimStock simStock = simStockData.findBySymbol(symbol);

        int priority = (p.getPositions().size()) + 1;

        System.out.println("\nPriority: " + priority);

        model.addAttribute("title", "Add Position");
        model.addAttribute("priority", priority);
        model.addAttribute("simStock", simStock);
        model.addAttribute(new PositionForm());
        return "position/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddPositionForm(@ModelAttribute @Valid PositionForm newPositionForm,
                                          Errors errors, Model model, @RequestParam int priority,
                                          @CookieValue(value = "portfolio", defaultValue = "-1") String portfolioId,
                                         @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        if(portfolioId.equals("-1")) {
            return "redirect:/portfolio";
        }

        if (newPositionForm.getSymbol().endsWith(","))
        {
            String symbol = newPositionForm.getSymbol().substring(0, (newPositionForm.getSymbol().length()-1));
            newPositionForm.setSymbol(symbol);
        }


        int pId = Integer.parseInt(portfolioId);
        Portfolio p = portfolioDao.findOne(pId);
        SimStock simStock = simStockData.findBySymbol(newPositionForm.getSymbol());

        if (errors.hasErrors()) {

            model.addAttribute("title", "Add Position");
            model.addAttribute("priority", priority);
            model.addAttribute("simStock", simStock);

            return "position/add";
        }

        Position newPosition = new Position(simStock, newPositionForm.getShares(), newPositionForm.getPercentage(),
                newPositionForm.isReinvest(), priority);

        newPosition.setPortfolio(p);

        positionDao.save(newPosition);
        p.addItem(newPosition);
        p.calcBalance();
        portfolioDao.save(p);
        return "redirect:view/" + newPosition.getId();
    }

    @RequestMapping(value = "view/{positionId}", method = RequestMethod.GET)
    public String viewPosition(Model model, @PathVariable int positionId,
                               @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        loadSimPositions(stockData);

        Position position = positionDao.findOne(positionId);
        SimStock simStock = simStockData.findBySymbol(position.getSymbol());

        if ((position.isValid() == false) || (simStock == null))
        {
            model.addAttribute("position", position);
            model.addAttribute("title", "");
            model.addAttribute("name", stockData.getAllSymbolsAndNames().get(position.getSymbol()));

            return "position/invalid";
        }

        model.addAttribute("position", position);
        model.addAttribute("simstock", simStock);
        model.addAttribute("title", "");

        return "position/view";
    }

    @RequestMapping(value = "viewlast/{positionId}", method = RequestMethod.GET)
    public String viewLastPosition(Model model, @PathVariable int positionId,
                               @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        loadSimPositions(stockData);

        Position position = positionDao.findOne(positionId);
        SimStock simStock = simStockData.findBySymbol(position.getSymbol());

        if ((position.isValid() == false) || (simStock == null))
        {
            model.addAttribute("position", position);
            model.addAttribute("title", "");
            model.addAttribute("name", stockData.getAllSymbolsAndNames().get(position.getSymbol()));

            return "position/lastInvalid";
        }

        model.addAttribute("position", position);
        model.addAttribute("simstock", simStock);
        model.addAttribute("title", "");

        return "position/viewLast";
    }

    @RequestMapping(value = "edit/{positionId}", method = RequestMethod.GET)
    public String displayEditForm(Model model, @PathVariable int positionId,
                                  @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        String title ="Edit a Position";
        Position changedPosition;

        model.addAttribute("title", title);

        changedPosition = positionDao.findOne(positionId);
        SimStock simStock = simStockData.findBySymbol(changedPosition.getSymbol());

        model.addAttribute("position", changedPosition);
        model.addAttribute("simStock", simStock);

        return "position/edit";
    }

    @RequestMapping(value = "edit/{positionId}", method = RequestMethod.POST)
    public String processEditPositionForm(@ModelAttribute @Valid Position changedPositionForm,
                                         Errors errors, Model model, @PathVariable int positionId,
                                         @CookieValue(value = "portfolio", defaultValue = "-1") String portfolioId,
                                         @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        if(portfolioId.equals("-1")) {
            return "redirect:/portfolio";
        }

        Position changedPosition = positionDao.findOne(positionId);
        SimStock simStock = simStockData.findBySymbol(changedPositionForm.getSymbol());

        if (errors.hasErrors()) {
            model.addAttribute("position", changedPosition);
            model.addAttribute("simStock", simStock);
            model.addAttribute("title", "Edit Position");

            return "position/edit";
        }


        changedPosition.setPercentage(changedPositionForm.getPercentage());
        changedPosition.setReinvest(changedPositionForm.isReinvest());

        if(changedPositionForm.getShares() > 0) {
            changedPosition.setShares(changedPositionForm.getShares());
            positionDao.save(changedPosition);
            changedPosition.getPortfolio().calcBalance();
            portfolioDao.save(changedPosition.getPortfolio());
            return "redirect:/position/view/" + changedPosition.getId();
        }
        else
        {
            changedPosition.getPortfolio().resetAfterDel();
            deletePosition(changedPosition);
            changedPosition.getPortfolio().calcBalance();
            portfolioDao.save(changedPosition.getPortfolio());
            return "redirect:/portfolio/view/" + changedPosition.getPortfolio().getId();
        }
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String removePosition(@RequestParam int positionId, @CookieValue(value = "user", defaultValue = "none") String username,
                                        @CookieValue(value = "portfolio", defaultValue = "none") String portfolioId) {

        if (username.equals("none")) {
            return "redirect:/user/login";
        }

        if (portfolioId.equals("none")) {
            return "redirect:/portfolio";
        }

        Integer pId = Integer.parseInt(portfolioId);
        Portfolio portfolio = portfolioDao.findOne(pId);

        Position oldPosition = positionDao.findOne(positionId);
        int oldId = oldPosition.getPortfolio().getId();

        deletePosition(oldPosition);

        portfolio.calcBalance();
        portfolioDao.save(portfolio);

        return "redirect:/portfolio/view/" + oldId;
    }
}
