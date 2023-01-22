package fr.ymanvieu.trading.scenario.framework.when;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.ymanvieu.test.scenario.when.WhenAction;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;

public abstract class AbstractWhenAction extends WhenAction<ScenarioContext> {

    private ObjectMapper objectMapper;

    protected ObjectMapper objectMapper() {
        if (this.objectMapper == null) {
            this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .setSerializationInclusion(Include.NON_NULL);
        }
        return this.objectMapper;
    }
}
