package fr.ymanvieu.trading.scenario.framework.then;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.assertj.core.api.ThrowingConsumer;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import fr.ymanvieu.trading.scenario.framework.when.GetLatestRates;
import fr.ymanvieu.trading.webapp.rate.dto.RateDTO;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Setter
@Accessors(fluent = true)
@ToString
public class GetLatestRatesVerification extends AbstractThenVerification {

    private List<Rate> rates;

    public GetLatestRatesVerification rates(Rate... rates) {
        this.rates = List.of(rates);
        return this;
    }

    @Override
    protected void internalVerify(ScenarioContext ctx) throws Exception {
        var getLatestRates = ctx.lastAction(GetLatestRates.class);

        getLatestRates.result()
            .andExpect(status().isOk());

        var result = getLatestRates.parseResult();

        if (rates != null) {
            var expectedRates = rates.stream().map(this::satisfies).toArray(Consumer[]::new);
            assertThat(result).satisfiesExactlyInAnyOrder(expectedRates);
        }
    }

    private ThrowingConsumer<RateDTO> satisfies(Rate rate) {
        return (result) -> {
            assertThat(result.getFromcur().getCode()).isEqualTo(rate.fromcurCode);
            assertThat(result.getTocur().getCode()).isEqualTo(rate.tocurCode);
            assertThat(result.getDate()).isEqualTo(rate.date);
            assertThat(result.getValue()).isEqualByComparingTo(BigDecimal.valueOf(rate.value));

            if (rate.favorite != null) {
                rate.favorite.ifPresentOrElse(
                    f -> assertThat(result.getFavorite()).isEqualTo(f),
                    () -> assertThat(result.getFavorite()).isNull());
            }
        };
    }

    @Accessors(fluent = true)
    public static class Rate {

        @Setter
        private String fromcurCode;
        @Setter
        private String tocurCode;
        @Setter
        private Instant date;
        @Setter
        private Double value;
        private Optional<Boolean> favorite;

        public Rate favorite(Boolean favorite) {
            this.favorite = Optional.ofNullable(favorite);
            return this;
        }
    }
}
