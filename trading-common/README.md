# Trading-common

## Data providers (see [provider.properties](src/main/resources/provider.properties) for URLs)

#### Yahoo Finance (for continuous currencies/stocks data and ticker lookup)
Free, no registration, JSON format, 172 currencies available and stocks.

#### European Central Bank (for historical currencies data)
Free, no registration, XML format, updated daily, most common currencies available.

#### Quandl (for crude oil daily/historical data)
Free, optional registration, JSON format, updated daily.

## Known issues
#### Yahoo provider
* For an unknown reason, Yahoo restrict the use of its service to mobile devices only. This is why there is a tweak in this class [Yahoo.java](src/main/java/fr/ymanvieu/trading/provider/rate/yahoo/Yahoo.java) to simulate an Android-based device. If you try to access with a computer, you will have a "Not a valid parameter" error and "406 Not Acceptable" HTTP response code.
* Also keep in mind that this Yahoo API is undocumented/unofficial and you can sometimes experienced HTTP errors like "404 Not Found", "502 Invalid HTTP Response" or "504 Maximum Transaction Time Exceeded".

## Todo
* Split this module in two modules to separate business and DAO layers
* Refactor/simplify architecture how to get data from other services (hide where the data comes from the outside)
