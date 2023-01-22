package fr.ymanvieu.trading.scenario.framework.then;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import fr.ymanvieu.trading.scenario.framework.when.GetLatestRate;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@ToString
public class GetLatestRateVerification extends AbstractThenVerification {

    private Rate expectedRate;

    @Override
    protected void internalVerify(ScenarioContext ctx) throws Exception {
        var getLatestRate = ctx.lastAction(GetLatestRate.class);

        getLatestRate.result()
            .andExpect(status().isOk());

        var result = getLatestRate.parseResult();

        if (expectedRate == null) {
            assertThat(result).isNull();
        } else {
            assertThat(result.getFromcur().getCode()).isEqualTo(expectedRate.fromcurCode);
            assertThat(result.getTocur().getCode()).isEqualTo(expectedRate.tocurCode);
            assertThat(result.getDate()).isEqualTo(expectedRate.date);
            assertThat(result.getValue()).isCloseTo(BigDecimal.valueOf(expectedRate.value), offset(BigDecimal.valueOf(0.0000000001)));
            assertThat(result.getFavorite()).isNull();
        }
    }

    @Setter
    @Accessors(fluent = true)
    @ToString
    public class Rate {

        private String fromcurCode;
        private String tocurCode;
        private Instant date;
        private Double value;

        public GetLatestRateVerification build() {
            GetLatestRateVerification.this.expectedRate = this;
            return GetLatestRateVerification.this;
        }
    }
}
