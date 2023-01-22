package fr.ymanvieu.trading.scenario.framework;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class ScenarioContext extends com.github.ymanvieu.test.scenario.ScenarioContext {

    private final DSL<ScenarioContext> dsl;
    private final ApplicationContext applicationContext;
    private final MockMvc mockMvc;

    public ScenarioContext(ApplicationContext applicationContext, MockMvc mockMvc) {
        this.applicationContext = applicationContext;
        this.mockMvc = mockMvc;
        this.dsl = new DSL<>(this);
    }

    public DSL<ScenarioContext> getDSL() {
        return dsl;
    }

    public <T> T getBean(Class<T> beanClass) throws BeansException {
        return applicationContext.getBean(beanClass);
    }

    public ResultActions performHttpRequest(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        return mockMvc.perform(requestBuilder).andDo(print());
    }

}
