package fr.ymanvieu.trading.scenario;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import fr.ymanvieu.trading.scenario.framework.given.Pair;
import fr.ymanvieu.trading.scenario.framework.then.GetLatestRateVerification;
import fr.ymanvieu.trading.scenario.framework.then.GetLatestRatesVerification;
import fr.ymanvieu.trading.scenario.framework.then.GetLatestRatesVerification.Rate;
import fr.ymanvieu.trading.scenario.framework.when.CreateRate;
import fr.ymanvieu.trading.scenario.framework.when.GetLatestRate;
import fr.ymanvieu.trading.scenario.framework.when.GetLatestRates;
import fr.ymanvieu.trading.scenario.framework.Scenario;

public class RateScenario extends Scenario {

    private static final Instant now = LocalDateTime.of(2022, 3, 4, 19, 18).atZone(ZoneId.of("Europe/Paris")).toInstant();
    private static final String EUR = "EUR";
    private static final String USD = "USD";

    @Test
    void getLatestRate() {

        when(new CreateRate()
            .fromCode(EUR)
            .toCode(USD)
            .date(now)
            .value(0.85));

        when(new GetLatestRate().fromcurCode(EUR).tocurCode(USD));
        verify(new GetLatestRateVerification().new Rate()
            .fromcurCode(EUR)
            .tocurCode(USD)
            .date(now)
            .value(0.85)
            .build());
    }

    @Test
    void getLatestRate_newPair() {

        given(new Pair().code("UBI.PA"));

        when(new CreateRate()
            .fromCode("UBI")
            .toCode(EUR)
            .date(now)
            .value(438.0));

        when(new GetLatestRate().fromcurCode("UBI").tocurCode(EUR));
        verify(new GetLatestRateVerification().new Rate()
            .fromcurCode("UBI")
            .tocurCode(EUR)
            .date(now)
            .value(438.0)
            .build());
    }

    @Test
    void getLatestRates_anonymous() {

        when(new CreateRate()
            .fromCode(EUR)
            .toCode(USD)
            .date(now)
            .value(0.85));

        when(new GetLatestRates());
        verify(new GetLatestRatesVerification()
            .rates(new Rate()
                .fromcurCode(EUR)
                .tocurCode(USD)
                .date(now)
                .value(0.85)
                .favorite(false)));
    }

    @WithMockUser(username = "1")
    @Test
    void getLatestRates_logged() {

        when(new CreateRate()
            .fromCode(EUR)
            .toCode(USD)
            .date(now)
            .value(0.85));

        when(new GetLatestRates());
        verify(new GetLatestRatesVerification()
            .rates(new Rate()
                .fromcurCode(EUR)
                .tocurCode(USD)
                .date(now)
                .value(0.85)
                .favorite(false)));
    }
}
