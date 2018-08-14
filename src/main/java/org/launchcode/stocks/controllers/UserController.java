package org.launchcode.stocks.controllers;

import org.launchcode.stocks.models.Hash;
import org.launchcode.stocks.models.Position;
import org.launchcode.stocks.models.Stock;
import org.launchcode.stocks.models.User;
import org.launchcode.stocks.models.data.PositionDao;
import org.launchcode.stocks.models.data.SimStockData;
import org.launchcode.stocks.models.data.StockData;
import org.launchcode.stocks.models.data.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * Created by LaunchCode
 */
@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserDao userdao;

    @Autowired
    private PositionDao positionDao;

    private StockData stockData = StockData.getInstance();

    private SimStockData simStockData = SimStockData.getInstance();

    @RequestMapping(value = "add")
    public String add(Model model) {

        model.addAttribute("title", "User Signup");
        User user = new User();
        model.addAttribute("user", user);
        return "user/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid User user, Errors errors, String verify) {
        List<User> sameName = userdao.findByUsername(user.getUsername());
        if(!errors.hasErrors() && user.getPassword().equals(verify) && sameName.isEmpty()) {
            String pswd = user.getPassword();
            String pswdHash = Hash.hashPassword(pswd);

            user.setPassword(pswdHash);
            model.addAttribute("user", user);
            userdao.save(user);
            return "redirect:/user/login";
        } else {
            model.addAttribute("user", user);
            model.addAttribute("title", "User Signup");
            if(!user.getPassword().equals(verify)) {
                model.addAttribute("message", "Passwords must match");
                user.setPassword("");
            }

            if(!sameName.isEmpty()) {
                model.addAttribute("message", "Username is taken, please select another one");
            }
            return "user/add";
        }
    }

    @RequestMapping(value = "login")
    public String loginForm(Model model) {

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

        model.addAttribute("title", "Login");
        model.addAttribute(new User());
        return "user/login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute User user, HttpServletResponse response) {
        List<User> u = userdao.findByUsername(user.getUsername());
        if(u.isEmpty()) {
            model.addAttribute("message", "Invalid Username");
            model.addAttribute("title", "Login");
            return "user/login";
        }

        User loggedIn = u.get(0);
        if(Hash.checkPassword(user.getPassword(), loggedIn.getPassword())) {

            Cookie c = new Cookie("user", user.getUsername());
            c.setPath("/");
            response.addCookie(c);
            return "redirect:/portfolio";
        } else {
            model.addAttribute("message", "Invalid Password");
            model.addAttribute("title", "Login");
            return "user/login";
        }
    }

    @RequestMapping(value = "logout")
    public String logout(javax.servlet.http.HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if(cookies != null) {
            System.out.println("\nRequest: " + request.toString());
            System.out.println("Cookies: " + cookies.length);
            for (Cookie c : cookies) {
                System.out.println("Cookie to be removed: " + c.getName() + " : " + c.getValue());
                c.setMaxAge(0);
                c.setPath("/");
                response.addCookie(c);
            }
        }
        return "redirect:/user/login";
    }
}
