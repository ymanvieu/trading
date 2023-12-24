package fr.ymanvieu.trading.scenario.framework;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.github.ymanvieu.test.scenario.FluentScenarioDSL;

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
@TestPropertySource(locations = "/application-scenario.properties")
@Sql("/scenario/sql/insert_data.sql")
@Import(ScenarioConfig.class)
public abstract class Scenario extends FluentScenarioDSL<ScenarioContext> {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    private ScenarioContext ctx;

    @BeforeEach
    public void setup() {
        this.ctx = new ScenarioContext(applicationContext, mockMvc, this);
        given(new ProviderRequestInterceptor());
        given(new RecaptchaRequestInterceptor());
    }

    @Override
    protected ScenarioContext getScenarioContext() {
        return ctx;
    }
}
