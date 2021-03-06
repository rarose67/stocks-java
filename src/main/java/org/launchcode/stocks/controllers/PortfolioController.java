package org.launchcode.stocks.controllers;

import org.launchcode.stocks.models.*;
import org.launchcode.stocks.models.data.*;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioDao portfolioDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PositionDao positionDao;

    /**
     * Delete the portfolio cookie
      * @param request
     * @param response
     */
    private void clearPortfolioCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if(cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("portfolio"))
                {
                    System.out.println("Cookie to be removed: " + c.getName() + " : " + c.getValue());
                    c.setMaxAge(0);
                    c.setPath("/");
                    response.addCookie(c);
                }

            }
        }
    }

    /**
     * Set a cookie that contains the id of the portfolio currently being accessed.
     * @param request
     * @param response
     * @param portfolioId - the portfolio id
     */
    private void setPortfolioCookie(HttpServletRequest request, HttpServletResponse response, int portfolioId) {

        Cookie[] cookies = request.getCookies();

        if(cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("portfolio"))
                {
                    return;
                }

            }
        }

        Integer pId = portfolioId;
        String portfolioIdString = pId.toString();
        Cookie c = new Cookie("portfolio", portfolioIdString);
        System.out.println("\nCookie added: " + c.getName() + " : " + c.getValue());
        c.setPath("/");
        response.addCookie(c);
    }

    private StockData stockData = StockData.getInstance();

    private SimStockData simStockData = SimStockData.getInstance();

    /**
     * This function create SimStock for all the positions in the database if they don't already exist.
     * @param stockData - A stockdata object used to access the data layer.
     */
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

    /**
     * Deletes the positions marked with 'deleted' status from the database.
     * @param portfolio - The portfolio with the positions to be deleted
     */
    private void clearDeleted(Portfolio portfolio)
    {
        ArrayList<Position> oldPositions = new ArrayList<>(portfolio.getPositions());

        for (Position position : oldPositions)
        {
            if (position.getState() == PositionState.DELETED)
            {
                portfolio.removeItem(position);
                positionDao.delete(position.getId());
                System.out.println("\nDelete Successful");
            }
        }
    }

    /**
     * Change the state of positions after a projection run.
     * @param portfolio - The portfolio with the positions to be updated
     */
    private void changeState(Portfolio portfolio)
    {
        for (Position position : portfolio.getPositions()) {
            if ((position.getState() == PositionState.NEW)) {
                position.setState(PositionState.ACTIVE);
                positionDao.save(position);

            } else if ((position.getState() != PositionState.ACTIVE)) {
                position.setState(PositionState.DELETED);
                positionDao.save(position);

            }
        }
    }

    // Request path: /portfolio

    /**
     * This page shows the portfolios for the user who is logged in.
     * @param model
     * @param request
     * @param response
     * @param username - The username of the logged in user.
     * @return
     */
    @RequestMapping(value = "")
    public String index(Model model,
                        HttpServletRequest request, HttpServletResponse response,
                        @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }
        User u = userDao.findByUsername(username).get(0);

        clearPortfolioCookie(request, response);
        model.addAttribute("portfolios", u.getPortfolios());
        model.addAttribute("title", "My Portfolios");

        return "portfolio/index";
    }

    /**
     * This page shows the form to add a portfolio for the user who is logged in.
     * @param model
     * @param username - The username of the logged in user.
     * @return
     */
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddPortfolioForm(Model model, @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        User u = userDao.findByUsername(username).get(0);

        model.addAttribute("title", "Add portfolio");
        model.addAttribute(new Portfolio());
        return "portfolio/add";
    }

    /**
     * This page processes the form to add a portfolio for the user who is logged in.
     * @param newPortfolio - An object containing the values of the new portfolio.
     * @param errors
     * @param model
     * @param username - The username of the logged in user.
     * @return
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddPortfolioForm(@ModelAttribute @Valid Portfolio newPortfolio,
                                          Errors errors, Model model,
                                          @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }
        User u = userDao.findByUsername(username).get(0);

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add portfolio");
            return "portfolio/add";
        }

        newPortfolio.setBalance(newPortfolio.getCash());
        newPortfolio.setUser(u);
        portfolioDao.save(newPortfolio);

        return "redirect:";
    }

    /**
     * This page shows the form to remove portfolios for the user who is logged in.
     * @param model
     * @param username - The username of the logged in user.
     * @return
     */
    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public String displayRemovePortfolioForm(Model model, @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }
        User u = userDao.findByUsername(username).get(0);

        model.addAttribute("portfolios", u.getPortfolios());
        model.addAttribute("title", "Remove portfolio");
        return "portfolio/remove";
    }

    /**
     * This page processes the form to remove portfolios for the user who is logged in.
     * @param portfolioIds An array of the portfolio ids to be deleted.
     * @param username - The username of the logged in user.
     * @return
     */
    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public String processRemovePortfolioForm(@RequestParam int[] portfolioIds, @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        for (int portfolioId : portfolioIds) {

            Portfolio portfolio = portfolioDao.findOne(portfolioId);
            ArrayList<Integer>  pIds = new ArrayList<>();

            for (Position position : portfolio.getPositions())
            {
                pIds.add(position.getId());
            }

            portfolio.getPositions().clear();

            for( int id : pIds)
            {
                positionDao.delete(id);
            }

            portfolioDao.delete(portfolioId);
        }

        return "redirect:";
    }

    /**
     * This page displays the current run of the portfolio
     * @param model
     * @param portfolioId
     * @param request
     * @param response
     * @param username
     * @return
     */
    @RequestMapping(value = "view/{portfolioId}", method = RequestMethod.GET)
    public String viewPortfolio(Model model, @PathVariable int portfolioId,
                                HttpServletRequest request, HttpServletResponse response,
                                @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        //loadSimPositions(stockData);

        Portfolio portfolio = portfolioDao.findOne(portfolioId);

        if (portfolio != null)
        {
            setPortfolioCookie(request, response, portfolioId);
        }

        List<Position> positionsList = positionDao.findCurrent(portfolio.getId());

        model.addAttribute("portfolio", portfolio);
        model.addAttribute("visablePositions", positionsList);
        model.addAttribute("stocks", stockData.getAllSymbolsAndNames());
        model.addAttribute("title", "Portfolio: " + portfolio.getName());

        return "portfolio/view";
    }

    /**
     * This page displays the previous run of the portfolio
     * @param model
     * @param portfolioId
     * @param request
     * @param response
     * @param username
     * @return
     */
    @RequestMapping(value = "viewlast/{portfolioId}", method = RequestMethod.GET)
    public String viewLastPortfolio(Model model, @PathVariable int portfolioId,
                                HttpServletRequest request, HttpServletResponse response,
                                @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        //loadSimPositions(stockData);

        Portfolio portfolio = portfolioDao.findOne(portfolioId);

        if (portfolio != null)
        {
            setPortfolioCookie(request, response, portfolioId);
        }

        List<Position> positionsList = positionDao.findLast(portfolio.getId());

        model.addAttribute("portfolio", portfolio);
        model.addAttribute("visablePositions", positionsList);
        model.addAttribute("stocks", stockData.getAllSymbolsAndNames());
        model.addAttribute("title", "Portfolio: " + portfolio.getName());

        return "portfolio/viewLast";
    }

    @RequestMapping(value = "add-item/{portfolioId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int portfolioId,
                          @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        Portfolio portfolio = portfolioDao.findOne(portfolioId);

        model.addAttribute("title", "Add an item to Portfolio: " + portfolio.getName());
        StockCompareType[] types = StockCompareType.values();
        model.addAttribute("types", types);
        model.addAttribute(new SearchForm());

        return "stock/search";
    }

    @RequestMapping(value = "remove-item", method = RequestMethod.GET)
    public String displayRemoveItemForm(Model model, @CookieValue(value = "user", defaultValue = "none") String username,
                                             @CookieValue(value = "portfolio", defaultValue = "none") String portfolioId) {

        if (username.equals("none")) {
            return "redirect:/user/login";
        }

        if (portfolioId.equals("none")) {
            return "redirect:/portfolio";
        }

        int pId = Integer.parseInt(portfolioId);
        Portfolio portfolio = portfolioDao.findOne(pId);

        model.addAttribute("positions", portfolio.getPositions());
        model.addAttribute("title", "Remove position");
        return "portfolio/remove-item";
    }

    @RequestMapping(value = "remove-item", method = RequestMethod.POST)
    public String processRemoveItemForm(@RequestParam int[] positionIds, @CookieValue(value = "user", defaultValue = "none") String username,
                                        @CookieValue(value = "portfolio", defaultValue = "none") String portfolioId) {

        if (username.equals("none")) {
            return "redirect:/user/login";
        }

        if (portfolioId.equals("none")) {
            return "redirect:/portfolio";
        }

        Integer pId = Integer.parseInt(portfolioId);
        Portfolio portfolio = portfolioDao.findOne(pId);

        for (int positionId : positionIds) {
            Position position = positionDao.findOne(positionId);
            portfolio.removeItem(position);
            positionDao.delete(positionId);
        }

        portfolio.calcBalance();

        portfolioDao.save(portfolio);

        return "redirect:view/"+ portfolio.getId();
    }

    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String displayEditForm(Model model,
                                  @CookieValue(value = "portfolio", defaultValue = "-1") String portfolioId,
                                  @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        if(portfolioId.equals("-1")) {
            return "redirect:/portfolio";
        }


        String title ="Edit a portfolio";
        Portfolio changedPortfolio;

        model.addAttribute("title", title);

        int pId = Integer.parseInt(portfolioId);
        changedPortfolio = portfolioDao.findOne(pId);


        model.addAttribute("portfolio", changedPortfolio);

        return "portfolio/edit";
    }

    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public String processEditportfolioForm(@ModelAttribute @Valid Portfolio changedPortfolioForm,
                                          Errors errors, Model model,
                                          @CookieValue(value = "portfolio", defaultValue = "-1") String portfolioId,
                                          @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        if(portfolioId.equals("-1")) {
            return "redirect:/portfolio";
        }

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add portfolio");

            return "portfolio/edit";
        }

        Integer pId = Integer.parseInt(portfolioId);
        Portfolio changedPortfolio = portfolioDao.findOne(pId);

        changedPortfolio.setName(changedPortfolioForm.getName());
        changedPortfolio.setCash(changedPortfolioForm.getCash());

        portfolioDao.save(changedPortfolio);
        return "redirect:/portfolio/view/" + changedPortfolio.getId();
    }

    @RequestMapping(value = "simulate", method = RequestMethod.POST)
    public String simulate(Model model, @RequestParam int years,
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

        //if(years > 0) {
          //  changeState(p);
            if (p.getYears() > 0) {
                p.reset();
            }
        //}

       // List<Position> positionsList =  positionDao.findValidByPortfolioAndNotState(p.getId(), PositionState.DELETED);
        List<Position> positionsList =  positionDao.findByPortfolio_idAndValidTrueAndStateNotOrderByPriorityAsc(p.getId(),
                PositionState.DELETED);

        p.calculate(years, positionsList);

        if(years > 0) {
            changeState(p);
        }

        p.setYears(years);

        clearDeleted(p);

        portfolioDao.save(p);
        return "redirect:view/" + p.getId();
    }
}
