package fr.ymanvieu.trading.scenario.framework.given;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import fr.ymanvieu.trading.scenario.framework.then.CreatePairVerification;
import fr.ymanvieu.trading.scenario.framework.when.CreatePair;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Accessors(fluent = true)
@Getter
public class Pair extends AbstractGivenParam {

    @Setter private String code;

    private Integer id;

    @Override
    protected void internalCreate(ScenarioContext ctx) {
        useAdmin();

        CreatePairVerification createdPair;

        ctx.getDSL()
            .when(new CreatePair()
                .code(code))
            .verify(createdPair = new CreatePairVerification());

        id = createdPair.getId();
    }
}
