package fr.ymanvieu.trading.scenario.framework.config;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.springframework.test.web.client.ExpectedCount.between;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.web.util.UriComponentsBuilder;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import fr.ymanvieu.trading.scenario.framework.given.AbstractGivenParam;

public class ProviderRequestInterceptor extends AbstractGivenParam {

    @Override
    public void reset(ScenarioContext ctx) {
        ctx.getBean(MockRestServiceServer.class).reset();
    }

    @Override
    protected void internalCreate(ScenarioContext ctx) {
        var server = ctx.getBean(MockRestServiceServer.class);

        //lookup
        server.expect(between(0, Integer.MAX_VALUE), requestTo(startsWith("https://finance.yahoo.com/_finance_doubledown/api/resource/searchassist"))).andRespond(createLookupResponse());
        //histo
        server.expect(between(0, Integer.MAX_VALUE), requestTo(startsWith("https://query1.finance.yahoo.com/v8/finance/chart/"))).andRespond(createHistoResponse());
        //latest
        server.expect(between(0, Integer.MAX_VALUE), requestTo(startsWith("https://query1.finance.yahoo.com/v7/finance/quote"))).andRespond(createLatestResponse());
    }

    ResponseCreator createLookupResponse() {
        return request -> {
            try {
                var pattern = Pattern.compile("/_finance_doubledown/api/resource/searchassist;searchTerm=(.*)");
                var matcher = pattern.matcher(request.getURI().getPath());
                matcher.matches();

                //TODO assert exists
                var searchTerm = matcher.group(1);

                var lookupResponse = new StringSubstitutor(
                    Map.of("symbol", searchTerm,
                        "name", searchTerm + " Inc."))
                    .replace(Files.readString(Path.of(new ClassPathResource("scenario/provider/lookup_template.json").getURI())));

                return withSuccess(lookupResponse, MediaType.APPLICATION_JSON).createResponse(request);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    ResponseCreator createHistoResponse() {
        return request -> {
            try {
                //TODO assert exists
                var pathFragments = UriComponentsBuilder.fromUri(request.getURI()).build().getPathSegments();
                var code = pathFragments.get(pathFragments.size() - 1);

                var histoResponse = new StringSubstitutor(
                    Map.of("symbol", code,
                        "currency", "USD"))
                    .replace(Files.readString(Path.of(new ClassPathResource("scenario/provider/histo_template.json").getURI())));

                return withSuccess(histoResponse, MediaType.APPLICATION_JSON).createResponse(request);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        };
    }


    ResponseCreator createLatestResponse() {
        return request -> {
            try {
                var params = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams();

                //TODO assert exists
                var symbols = params.getFirst("symbols");

                var latestResponse = new StringSubstitutor(
                    Map.of("symbol", symbols,
                        "regularMarketPrice", 1,
                        "regularMarketTime", Instant.now().getEpochSecond()))
                    .replace(Files.readString(Path.of(new ClassPathResource("scenario/provider/latest_template.json").getURI())));

                return withSuccess(latestResponse, MediaType.APPLICATION_JSON).createResponse(request);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}
