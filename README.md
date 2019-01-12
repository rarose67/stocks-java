# stocks-java
My Stock Simulator

## Overview
I wanted to make a website where a member could take a simStock that gives a dividend undetermined how many shares of that simStock they would have after reinvesting the dividends for so many years as well as the overall value of their position. This website will be able to give users the ability to create a portfolio of different positions and allow them to project the value of that portfolio out a given number of years.
I have seen other sites where users can determine the amount of income given by dividends, but these sites only allow a member to determine this based on static values and do not allow projection into the future.

### Features
* User login: Users will be able to create accounts and log in to the application.
* Database:  The application stores the users different simStock positions, balance information, and years projected.
* Search: The member will able to search for stocks by symbol, name, price, dividend, yield, and dividend date.
* Data projection:  The app will simulate dividend reinvestment by using the current price data to project what the future price might be   based on a random number generator.
* API interface: Retrieve simStock data via IEX API

### Technologies
*	Java
*   Thymeleaf
*   MySQL
*   JavaScript
*   JQuery