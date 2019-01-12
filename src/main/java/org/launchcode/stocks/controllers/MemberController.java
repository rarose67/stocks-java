package org.launchcode.stocks.controllers;

import org.launchcode.stocks.models.Hash;
import org.launchcode.stocks.models.Member;
import org.launchcode.stocks.models.data.PositionDao;
import org.launchcode.stocks.models.data.SimStockData;
import org.launchcode.stocks.models.data.StockData;
import org.launchcode.stocks.models.data.MemberDao;
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
public class MemberController {

    @Autowired
    private MemberDao MemberDao;

    @Autowired
    private PositionDao positionDao;

    private StockData stockData = StockData.getInstance();

    private SimStockData simStockData = SimStockData.getInstance();

    @RequestMapping(value = "add")
    public String add(Model model) {

        model.addAttribute("title", "Member Signup");
        Member member = new Member();
        model.addAttribute("member", member);
        return "member/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid Member member, Errors errors, String verify) {
        List<Member> sameName = MemberDao.findByUsername(member.getUsername());
        if(!errors.hasErrors() && member.getPassword().equals(verify) && sameName.isEmpty()) {
            String pswd = member.getPassword();
            String pswdHash = Hash.hashPassword(pswd);

            member.setPassword(pswdHash);
            model.addAttribute("member", member);
            MemberDao.save(member);
            return "redirect:/member/login";
        } else {
            model.addAttribute("member", member);
            model.addAttribute("title", "Member Signup");

            if(member.getPassword().length() < 6) {
                model.addAttribute("message", "Password must be a least 6 characters long.");
            }

            if((!member.getPassword().equals(verify)) && (member.getPassword().length() >= 6))  {
                model.addAttribute("message", "Passwords must match");
                member.setPassword("");
            }

            if((member.getEmail().equals("")) ||
                    !(member.getEmail().matches("[\\w-]+@[\\w-]+\\.\\w+"))) {
                model.addAttribute("message", "Please enter a valid Email.");
            }

            if((member.getUsername().equals("")) ||
                    !(member.getUsername().matches("[a-zA-Z]{5}[a-zA-Z]*"))) {
                model.addAttribute("message", "The username must be at least 5 characters, & must be alphabetic");
            }

            if(!sameName.isEmpty()) {
                model.addAttribute("message", "Username is taken, please select another one");
            }
            return "member/add";
        }
    }

    @RequestMapping(value = "login")
    public String loginForm(Model model) {

       /** if (simStockData.findAll().isEmpty()) {
            List<String> symbols = positionDao.findSymbols();
            Stock stock;

            for (String symbol : symbols) {
                stock = stockData.findBySymbol(symbol);
                if (stock != null) {
                    simStockData.add(stock);
                }
            }
        } */

        model.addAttribute("title", "Login");
        model.addAttribute(new Member());
        return "user/login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute Member member, HttpServletResponse response) {
        List<Member> u = MemberDao.findByUsername(member.getUsername());
        if(u.isEmpty()) {
            model.addAttribute("message", "Invalid Username");
            model.addAttribute("title", "Login");
            return "member/login";
        }

        Member loggedIn = u.get(0);
        if(Hash.checkPassword(member.getPassword(), loggedIn.getPassword())) {

            Cookie c = new Cookie("member", member.getUsername());
            c.setPath("/");
            response.addCookie(c);
            return "redirect:/portfolio";
        } else {
            model.addAttribute("message", "Invalid Password");
            model.addAttribute("title", "Login");
            return "member/login";
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
