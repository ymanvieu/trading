package fr.ymanvieu.trading.scenario.framework;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.PrintingResultHandler;
import org.springframework.util.CollectionUtils;
import com.github.ymanvieu.test.scenario.FluentScenarioDSL;

public class ScenarioContext extends com.github.ymanvieu.test.scenario.ScenarioContext {

    private static final Logger log = LoggerFactory.getLogger("fr.ymanvieu.trading.scenario.framework.when.result");

    private final ApplicationContext applicationContext;
    private final MockMvc mockMvc;
    private final FluentScenarioDSL<ScenarioContext> dsl;

    public ScenarioContext(ApplicationContext applicationContext, MockMvc mockMvc, FluentScenarioDSL<ScenarioContext> dsl) {
        this.applicationContext = applicationContext;
        this.mockMvc = mockMvc;
        this.dsl = dsl;
    }

    public <T> T getBean(Class<T> beanClass) throws BeansException {
        return applicationContext.getBean(beanClass);
    }

    public ResultActions performHttpRequest(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        return mockMvc.perform(requestBuilder).andDo(new LoggingResultHandler());
    }

    public FluentScenarioDSL<ScenarioContext> getDSL() {
        return dsl;
    }

    private static class PrintWriterPrintingResultHandler extends PrintingResultHandler {

        public PrintWriterPrintingResultHandler(PrintWriter writer) {
            super(new ResultValuePrinter() {
                @Override
                public void printHeading(String heading) {
                    writer.println();
                    writer.println(String.format("%s:", heading));
                }
                @Override
                public void printValue(String label, @Nullable Object value) {
                    if (value != null && value.getClass().isArray()) {
                        value = CollectionUtils.arrayToList(value);
                    }
                    writer.println(String.format("%17s = %s", label, value));
                }
            });
        }
    }

    private static class LoggingResultHandler implements ResultHandler {

        @Override
        public void handle(MvcResult result) throws Exception {
            if (log.isDebugEnabled()) {
                StringWriter stringWriter = new StringWriter();
                ResultHandler printingResultHandler =
                    new PrintWriterPrintingResultHandler(new PrintWriter(stringWriter));
                printingResultHandler.handle(result);
                log.debug("MvcResult details:\n" + stringWriter);
            }
        }
    }
}
