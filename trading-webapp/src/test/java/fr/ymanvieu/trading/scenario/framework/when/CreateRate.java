package fr.ymanvieu.trading.scenario.framework.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.google.common.base.Preconditions;

import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.datacollect.rate.RateUpdaterService;
import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Setter
@Accessors(fluent = true)
@ToString
public class CreateRate extends AbstractWhenAction {

    private String fromCode;
    private String toCode;
    private Instant date;
    private Double value;

    @Override
    protected void internalExecute(ScenarioContext ctx) {
        Preconditions.checkNotNull(fromCode);
        Preconditions.checkNotNull(toCode);
        Preconditions.checkNotNull(date);
        Preconditions.checkNotNull(value);

        Quote quote = new Quote(fromCode, toCode, new BigDecimal(value), date);

        //TODO use ProviderRequestInterceptor then call global updateRates(LatestrateProvider)
        ctx.getBean(RateUpdaterService.class).updateRates(List.of(quote));
    }
}
