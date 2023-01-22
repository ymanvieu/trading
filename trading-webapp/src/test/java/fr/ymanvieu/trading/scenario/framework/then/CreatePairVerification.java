package fr.ymanvieu.trading.scenario.framework.then;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import fr.ymanvieu.trading.scenario.framework.when.CreatePair;
import lombok.Getter;

public class CreatePairVerification extends AbstractThenVerification {

    @Getter private Integer id;

    @Override
    protected void internalVerify(ScenarioContext ctx) throws Exception {
        var createPair = ctx.lastAction(CreatePair.class);

        createPair.result()
            .andExpect(status().isOk());

        var result = createPair.parseResult();

        assertThat(result.getId()).isNotNull();
        assertThat(result.getCode()).isNotNull();
        assertThat(result.getName()).isNotNull();
        assertThat(result.getQuote()).hasNoNullFieldsOrProperties();

        id = result.getId();
    }
}
