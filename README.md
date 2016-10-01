#Trading
Running demo: 
<https://cloud-app.one>

This project has 3 goals : 

Collecting financial data from providers (see list below): 
* Currencies and stocks : every 5 min 
* Crude oil (Brent) : everyday at 6 AM (UTC)

Displaying data in a Web-based UI: 
* Latest collected data
* Historical data with an interactive chart

Make orders with virtual money (free registration):
* Buy/sell stocks and currencies

------------------------------------------------------------

The Web UI is based on Thymeleaf, AngularJS with Highchart (Highstock), Bootstrap and SockJS (STOMP). 
The Backend is based on Spring-boot 1.3 (JRE 7+).

Users registration secured with anti-bot [Google reCAPTCHA 2](https://www.google.com/recaptcha/intro/index.html)

#### Data provider

**Yahoo Finance** (for continuous currencies/stocks data and ticker lookup)

Free, no registration, JSON format, 172 currencies available and stocks.

**European Central Bank** (for historical forex data)

Free, no registration, XML format, updated daily at 3pm (CET), most common currencies available.

**Quandl** (for crude oil daily/historical data)

Free, optional registration, JSON format, updated daily.

#### Todo
* change default currency from US dollar to Euro
* add metals market data
* add continuous market data for oil (using yahoo futures (front + "month+1"))
