package org.launchcode.stocks.controllers;

import org.launchcode.stocks.models.Portfolio;
import org.launchcode.stocks.models.Position;
import org.launchcode.stocks.models.StockCompareType;
import org.launchcode.stocks.models.User;
import org.launchcode.stocks.models.data.PortfolioDao;
import org.launchcode.stocks.models.data.PositionDao;
import org.launchcode.stocks.models.data.UserDao;
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

@Controller
@RequestMapping("portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioDao portfolioDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PositionDao positionDao;

    private void clearPortfolioCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if(cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("portfolio"))
                {
                    c.setMaxAge(0);
                    c.setPath("/");
                    response.addCookie(c);
                }

            }
        }
    }

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
        c.setPath("/");
        response.addCookie(c);
    }

    // Request path: /portfolio
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

    @RequestMapping(value = "view/{portfolioId}", method = RequestMethod.GET)
    public String viewPortfolio(Model model, @PathVariable int portfolioId,
                                HttpServletRequest request, HttpServletResponse response,
                                @CookieValue(value = "user", defaultValue = "none") String username) {

        if(username.equals("none")) {
            return "redirect:/user/login";
        }

        Portfolio portfolio = portfolioDao.findOne(portfolioId);

        if (portfolio != null)
        {
            setPortfolioCookie(request, response, portfolioId);
        }

        model.addAttribute("portfolio", portfolio);
        model.addAttribute("title", "Portfolio: " + portfolio.getName());

        return "portfolio/view";
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

        return "redirect:view/"+ portfolio.getId();
    }
}
