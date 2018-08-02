package org.launchcode.stocks.controllers;

import org.launchcode.stocks.models.Portfolio;
import org.launchcode.stocks.models.Position;
import org.launchcode.stocks.models.SimStock;
import org.launchcode.stocks.models.StockCompareType;
import org.launchcode.stocks.models.data.PortfolioDao;
import org.launchcode.stocks.models.data.PositionDao;
import org.launchcode.stocks.models.data.SimStockData;
import org.launchcode.stocks.models.data.UserDao;
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

@Controller
@RequestMapping("position")
public class PositionController {

    @Autowired
    private PortfolioDao portfolioDao;

    @Autowired
    private PositionDao positionDao;

    @Autowired
    private UserDao userDao;

    private  SimStockData simStockData = SimStockData.getInstance();

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddPositionForm(Model model, @RequestParam String symbol,
                                         @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        SimStock simStock = simStockData.findBySymbol(symbol);

        model.addAttribute("title", "Add Position");
        model.addAttribute("simStock", simStock);
        model.addAttribute(new PositionForm());
        return "position/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddPositionForm(@ModelAttribute @Valid PositionForm newPositionForm,
                                          Errors errors, Model model,
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

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Position");

            return "position/add";
        }

        SimStock stock = simStockData.findBySymbol(newPositionForm.getSymbol());


        Position newPosition = new Position(stock, newPositionForm.getShares(), newPositionForm.getPercentage(),
                newPositionForm.isReinvest());

        newPosition.setPortfolio(p);

        positionDao.save(newPosition);
        p.addItem(newPosition);
        return "redirect:view/" + newPosition.getId();
    }

    @RequestMapping(value = "view/{positionId}", method = RequestMethod.GET)
    public String viewPosition(Model model, @PathVariable int positionId,
                               @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        Position position = positionDao.findOne(positionId);
        SimStock simStock = simStockData.findBySymbol(position.getSymbol());

        model.addAttribute("position", position);
        model.addAttribute("simstock", simStock);
        model.addAttribute("title", "");

        return "position/view";
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


        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Position");

            return "position/edit";
        }

        Position changedPosition = positionDao.findOne(positionId);
        SimStock stock = simStockData.findBySymbol(changedPositionForm.getSymbol());

        changedPosition.setShares(changedPositionForm.getShares());
        changedPosition.setPercentage(changedPositionForm.getPercentage());
        changedPosition.setReinvest(changedPositionForm.isReinvest());

        positionDao.save(changedPosition);
        return "redirect:/position/view/" + changedPosition.getId();
    }
}
