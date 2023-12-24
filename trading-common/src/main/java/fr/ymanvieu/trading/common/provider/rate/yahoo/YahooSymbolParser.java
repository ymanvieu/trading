package fr.ymanvieu.trading.common.provider.rate.yahoo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.ymanvieu.trading.common.symbol.Currency;

public class YahooSymbolParser {

    private static final Pattern EQUITY_PATTERN = Pattern.compile("([\\w_-]+)[.\\w*]*");
    private static final Pattern FOREX_PATTERN = Pattern.compile("(\\w{3})(\\w{3})?=X");
    private static final Pattern FUTURE_PATTERN = Pattern.compile("(\\w+=F)");
    private static final Pattern CRYPTOCURRENCY_PATTERN = Pattern.compile("(\\w{3,4})-(\\w{3,4})");

    public static String parseSource(String code) {
        Matcher forexMatcher = FOREX_PATTERN.matcher(code);

        if (forexMatcher.matches()) {
            String source = forexMatcher.group(1);
            String target = forexMatcher.group(2);
            return (target != null) ? source : Currency.USD;
        }

        Matcher cryptoMatcher = CRYPTOCURRENCY_PATTERN.matcher(code);

        if (cryptoMatcher.matches()) {
            return cryptoMatcher.group(1);
        }

        Matcher stockMatcher = EQUITY_PATTERN.matcher(code);

        if (stockMatcher.matches()) {
            return stockMatcher.group(1);
        }

        Matcher futureMatcher = FUTURE_PATTERN.matcher(code);

        if (futureMatcher.matches()) {
            return futureMatcher.group(1);
        }

        return null;
    }

    public static String parseTarget(String code) {
        Matcher forexMatcher = FOREX_PATTERN.matcher(code);

        if (forexMatcher.matches()) {
            String source = forexMatcher.group(1);
            String target = forexMatcher.group(2);
            return (target != null) ? target : source;
        }

        Matcher cryptoMatcher = CRYPTOCURRENCY_PATTERN.matcher(code);

        if (cryptoMatcher.matches()) {
            return cryptoMatcher.group(2);
        }

        return null;
    }

}
