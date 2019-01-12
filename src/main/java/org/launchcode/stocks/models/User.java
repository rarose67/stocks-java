package org.launchcode.stocks.models;

import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * This class defines the user object which contains information about registered users.
 */
@Entity(name = "user")
public class User {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    @Size(min = 5, max = 15)
    @Pattern(regexp = "[a-zA-Z]{5}[a-zA-Z]*",
            message = "The username must be at least 5 characters, & must be alphabetic")
    private String username;

    @Email
    private String email;

    @NotNull
    @Size(min = 6)
    private String password;

    @OneToMany
    @JoinColumn(name = "user_id")
    private List<Portfolio> portfolios;

    public User() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Portfolio> getPortfolios() {return portfolios;}

    public void addItem(Portfolio item)
    {
        this.portfolios.add(item);
    }
}

