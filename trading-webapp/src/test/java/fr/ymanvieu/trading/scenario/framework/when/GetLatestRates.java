package fr.ymanvieu.trading.scenario.framework.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.core.type.TypeReference;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import fr.ymanvieu.trading.webapp.rate.dto.RateDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Setter
@Accessors(fluent = true)
@ToString(exclude = "result")
public class GetLatestRates extends AbstractWhenAction {

    @Getter
    private ResultActions result;

    @Override
    protected void internalExecute(ScenarioContext ctx) throws Exception {
        result = ctx.performHttpRequest(get("/api/rate/latest"));
    }

    public List<RateDTO> parseResult() {
        try {
            return objectMapper().readValue(
                result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
