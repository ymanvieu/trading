package fr.ymanvieu.trading.scenario.framework.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.Validate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.core.type.TypeReference;

import fr.ymanvieu.trading.common.admin.PairInfo;
import fr.ymanvieu.trading.common.provider.UpdatedPair;
import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@ToString(exclude = "result")
public class UpdatePair extends AbstractWhenAction {

    @Setter private Integer id;
    @Setter private String symbol;

    @Getter
    private ResultActions result;

    @Override
    protected void internalExecute(ScenarioContext ctx) throws Exception {
        Validate.notNull(id);
        Validate.notEmpty(symbol);

        var updatedPair = new UpdatedPair().setId(id).setSymbol(symbol);

        result = ctx.performHttpRequest(put("/api/admin")
            .content(objectMapper().writeValueAsBytes(updatedPair))
            .contentType(MediaType.APPLICATION_JSON));
    }


    public PairInfo parseResult() {
        try {
            return objectMapper().readValue(
                result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
