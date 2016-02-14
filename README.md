#Forex
Running demo: 
<https://srv.cloud-app.pw/>

This project has 2 goals : 

Collecting market data from providers (see list below): 
* Currencies and stocks : every 5 min 
* Crude oil (Brent) : every 6 hours

Displaying data in a Web-based UI: 
* Latest collected data
* Historical data with an interactive chart

------------------------------------------------------------

The Web UI is based on AngularJS with Highchart (Highstock), Bootstrap and SockJS. The Backend is based on Spring-boot 1.3 (JRE 7+).

#### Data provider

** Yahoo Finance ** (for continous currencies/stocks data)

Free, no registration, JSON format, 172 currencies available and stocks.

** European Central Bank ** (for historical forex data)

Free, no registration, XML format, updated daily at 3pm (CET), most common currencies available.

** Quandl ** (for crude oil daily/historical data)

Free, no registration, JSON format, updated daily.

#### Todo
* use elastic search
* change default currency from US dollar to Euro
* add metals market data
* add continuous market data for oil (using yahoo futures (front + "month+1")
* remove discontinued currencies (old european currencies)
* can make virtual buy/sell transactions