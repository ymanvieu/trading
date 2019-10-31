# Trading

Trading is an application collecting financial data from an external provider and allow users to use their virtual portofolio.

## Live demo
<https://cloud-app.one>

## Modules
### trading-ng2
Web interface containing the following screens : 
* Index: Latest collected data 
* Chart: Historical data with an interactive chart
* Portofolio: Buy/sell stocks and currencies
* Admin: add/remove stocks to collect (ADMIN users only, search is based on Yahoo tickers)

### trading-data-collect
Collecting financial data from provider (Yahoo) every 5 min (customizable)

### trading-webapp
Rest API endpoints

### trading-common
Contains the business and DAO layers as well as implementation of financial data provider.

## Requirements
Trading requires Java 11.

## Building from Source

Trading uses Maven for its build. To build the complete project run

    mvn clean install

## Under the hood
* Frontend is powered by Angular 8 with Highcharts 7.1 and Clarity Design. 
* Backend is powered by Spring-boot 2.2 and use a lot of Spring modules (spring-web, spring-security, spring-data-jpa, ...) and third-party dependencies like ActiveMQ, Querydsl, Lombok, Mapstruct...
* Persistence is configured to use H2DB in dev-mode and MySQL otherwise, but can use other DBMS in no time by switching the database driver dependency and editing the configuration file.
* Users registration is secured with [Google reCAPTCHA 2](https://www.google.com/recaptcha/intro/index.html) (optional)

## Improvements
* Customizable default portofolio currency
* implement a trading bot
* Suggestions appreciated :)