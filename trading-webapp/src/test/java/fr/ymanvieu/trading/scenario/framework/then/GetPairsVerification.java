package fr.ymanvieu.trading.scenario.framework.then;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import fr.ymanvieu.trading.scenario.framework.when.GetPairs;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class GetPairsVerification extends AbstractThenVerification {

    @Setter int resultSize = 1;
    @Setter int existingPairsSize = 0;

    @Override
    protected void internalVerify(ScenarioContext ctx) throws Exception {
        var action = ctx.lastAction(GetPairs.class);

        action.result()
            .andExpect(status().isOk());

        var result = action.parseResult();

        assertThat(result.existingPairs().size() + result.availableSymbols().size()).isEqualTo(resultSize);
        assertThat(result.existingPairs()).hasSize(existingPairsSize);
    }
}
