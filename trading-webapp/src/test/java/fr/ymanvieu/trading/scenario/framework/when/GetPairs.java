package fr.ymanvieu.trading.scenario.framework.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;

import fr.ymanvieu.trading.common.admin.SearchResult;
import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@ToString(exclude = "result")
public class GetPairs extends AbstractWhenAction {

    @Setter
    private String code;

    @Getter
    private ResultActions result;

    @Override
    protected void internalExecute(ScenarioContext ctx) throws Exception {
        Preconditions.checkNotNull(code);

        result = ctx.performHttpRequest(get("/api/admin")
            .param("code", code));
    }

    public SearchResult parseResult() {
        try {
            return objectMapper().readValue(
                result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
