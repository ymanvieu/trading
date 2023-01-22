package fr.ymanvieu.trading.scenario.framework.config;

import static org.springframework.test.web.client.ExpectedCount.between;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import fr.ymanvieu.trading.scenario.framework.given.AbstractGivenParam;

public class RecaptchaRequestInterceptor extends AbstractGivenParam {

    @Override
    public void reset(ScenarioContext ctx) {
        ctx.getBean(MockRestServiceServer.class).reset();
    }

    @Override
    protected void internalCreate(ScenarioContext ctx) throws Exception {
        var server = ctx.getBean(MockRestServiceServer.class);
        server
            .expect(between(0, Integer.MAX_VALUE), requestTo("https://www.google.com/recaptcha/api/siteverify"))
            .andRespond(withSuccess(Files.readString(Path.of(new ClassPathResource("scenario/recaptcha/success.json").getURI())),
                MediaType.APPLICATION_JSON));
    }
}
