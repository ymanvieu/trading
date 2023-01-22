package fr.ymanvieu.trading.scenario.framework.then;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import fr.ymanvieu.trading.scenario.framework.when.DeletePair;

public class DeletePairVerification extends AbstractThenVerification {

    @Override
    protected void internalVerify(ScenarioContext ctx) throws Exception {
        var deletePair = ctx.lastAction(DeletePair.class);

        deletePair.result()
            .andExpect(status().isOk());
    }
}
