package fr.ymanvieu.trading.datacollect.rate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import fr.ymanvieu.trading.common.rate.event.RatesUpdatedEvent;

@Component
@ConditionalOnProperty(value = "spring.jms.listener.auto-startup", matchIfMissing = true)
public class RatesUpdatedEventListener {

    @Autowired
    private JmsTemplate jms;

    @Async
    @EventListener
    public void send(RatesUpdatedEvent event) {
        jms.convertAndSend("trading.rate.latest", event);
    }
}
