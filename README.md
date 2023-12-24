# Trading [![Build](https://github.com/ymanvieu/trading/actions/workflows/maven.yml/badge.svg)](https://github.com/ymanvieu/trading/actions/workflows/maven.yml)

Trading is an application collecting financial data from an external provider and allow users to manage their virtual portofolio.

## Live demo
<https://cloud-app.one>


## Requirements
Java

    JDK 21+ and Maven 3.8+

Angular: 

    Node 20+ and Yarn 3.2+

## Build from Sources

Java

    mvn clean install
    cd trading-webapp
    mvn spring-boot:run

Angular

    cd trading-ng
    yarn install
    yarn start

## Modules
### trading-ng
Angular app containing the following screens :
* Index: Latest collected data
* Chart: Historical data with an interactive chart
* Portofolio: Buy/sell stocks and currencies
* Admin: add/remove stocks to collect (ADMIN users only, search is based on Yahoo tickers)

### trading-data-collect
Collects financial data from provider (Yahoo) every 5 min (customizable)

### trading-webapp
Rest API endpoints

### trading-common
Business and DAO layers as well as implementation of financial data provider.


## Under the hood
* Frontend is powered by Angular 17 with PrimeNG and Highcharts. Testing is done with Cypress E2E/Component and Jest.
* Backend is powered by Spring-boot 3.2 and use a lot of Spring modules (spring-web, spring-security, spring-data-jpa, spring-oauth2...) and third-party dependencies like ArtemisMQ, Querydsl, Lombok, Mapstruct...
* Persistence is configured to use H2DB in dev-mode/tests and PostgreSQL otherwise, but can use other DBMS by switching the database driver dependency and editing the configuration file.
* (optional) User registration is secured with [Google reCAPTCHA 2](https://www.google.com/recaptcha/intro/index.html)
* (optional) User social login with Google (OpenID Connect) or Github (OAuth 2)
