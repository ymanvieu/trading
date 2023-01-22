package fr.ymanvieu.trading.scenario.framework.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import org.springframework.test.web.servlet.ResultActions;
import com.google.common.base.Preconditions;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@ToString(exclude = "result")
public class DeletePair extends AbstractWhenAction {

    @Setter
    private Integer id;

    @Setter
    private Boolean withSymbol;

    @Getter
    private ResultActions result;

    @Override
    protected void internalExecute(ScenarioContext ctx) throws Exception {
        Preconditions.checkNotNull(id);

        var requestBuilder = delete("/api/admin/{id}", id);

        if (withSymbol != null) {
            requestBuilder.param("withSymbol", String.valueOf(withSymbol));
        }

        result = ctx.performHttpRequest(requestBuilder);
    }
}
