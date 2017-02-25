# Trading

Trading is an application collecting financial data from several providers and allow users to play with their virtual portofolio.

## Live demo
<https://cloud-app.one>

## Modules
### trading-data-collect
Collecting financial data from providers: 
* Currencies and stocks : every 5 min 
* Crude oil (Brent) : everyday at 8 AM (UTC)

### trading-webapp
Responsive Web inteface containing the following screens : 
* Index: Latest collected data 
* Chart: Historical data with an interactive chart
* Portofolio: Buy/sell stocks and currencies
* Admin: add/remove stocks to collect (ADMIN users only, search is based on Yahoo tickers)

### trading-common
Contains the business and DAO layers as well as the implementation how financial data is collected.

## Requirements
Trading requires Java 1.8+.

## Building from Source

Trading uses Maven for its build. To build the complete project run

    mvn clean install

## Under the hood
* Frontend is powered by Thymeleaf, AngularJS with Highchart (Highstock), Bootstrap and SockJS (STOMP). 
* Backend is powered by Spring-boot 1.5 and use a lot of Spring modules (spring-web, spring-security, spring-data-jpa, spring-session, etc...) and third-party dependencies like Querydsl, Guava, Lombok, Mapstruct...
* Persistence is configured to use H2DB in dev-mode and MySQL otherwise, but can use other DBMS in no time by switching the database driver dependency and editing the main configuration file.
* Users registration is secured with [Google reCAPTCHA 2](https://www.google.com/recaptcha/intro/index.html) (optional)

## Todo : Because the road never ends
* Configurable default portofolio currency
* add metals market data (which provider ?)
* implement a trading bot
* Suggestions appreciated :)