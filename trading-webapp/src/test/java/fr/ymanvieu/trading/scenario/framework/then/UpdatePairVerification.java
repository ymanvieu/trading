package fr.ymanvieu.trading.scenario.framework.then;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import fr.ymanvieu.trading.scenario.framework.when.UpdatePair;

public class UpdatePairVerification extends AbstractThenVerification {

    @Override
    protected void internalVerify(ScenarioContext ctx) throws Exception {
        var action = ctx.lastAction(UpdatePair.class);

        action.result()
            .andExpect(status().isOk());
    }
}
