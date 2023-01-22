package fr.ymanvieu.trading.scenario.framework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.github.ymanvieu.test.scenario.ScenarioDSL;
import com.github.ymanvieu.test.scenario.given.GivenParam;
import com.github.ymanvieu.test.scenario.then.ThenVerification;
import com.github.ymanvieu.test.scenario.tool.ToolAction;
import com.github.ymanvieu.test.scenario.when.WhenAction;

import fr.ymanvieu.trading.common.TradingCommonApplication;
import fr.ymanvieu.trading.datacollect.TradingDataCollectApplication;
import fr.ymanvieu.trading.scenario.framework.config.ProviderRequestInterceptor;
import fr.ymanvieu.trading.scenario.framework.config.RecaptchaRequestInterceptor;
import fr.ymanvieu.trading.scenario.framework.config.ScenarioConfig;
import fr.ymanvieu.trading.webapp.TradingWebApplication;

@SpringBootTest
@Transactional
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@AutoConfigureWebClient
@ContextConfiguration(classes = {
    TradingWebApplication.class,
    TradingCommonApplication.class,
    TradingDataCollectApplication.class,
})
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
    "spring.jpa.generate-ddl=true",
    "spring.flyway.enabled=false",
    "spring.jms.listener.auto-startup=false",
    "trading.scheduler.type=none"
})
@Sql("/scenario/sql/insert_data.sql")
@Import(ScenarioConfig.class)
public abstract class Scenario implements ScenarioDSL<ScenarioContext> {

    private ScenarioContext ctx;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.ctx = new ScenarioContext(applicationContext, mockMvc);
        given(new ProviderRequestInterceptor());
        given(new RecaptchaRequestInterceptor());
    }

    @SafeVarargs
    @Override
    public final ScenarioDSL<ScenarioContext> given(GivenParam<ScenarioContext>... givenParam) {
        return ctx.getDSL().given(givenParam);
    }

    @Override
    public ScenarioDSL<ScenarioContext> when(WhenAction<ScenarioContext> whenAction) {
        return ctx.getDSL().when(whenAction);
    }

    @Override
    public ScenarioDSL<ScenarioContext> verify(ThenVerification<ScenarioContext> thenVerification) {
        return ctx.getDSL().verify(thenVerification);
    }

    @Override
    public ScenarioDSL<ScenarioContext> util(ToolAction<ScenarioContext> toolAction) {
        return ctx.getDSL().util(toolAction);
    }


}
