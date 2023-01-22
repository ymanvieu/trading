package fr.ymanvieu.trading.webapp.rate.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import fr.ymanvieu.trading.common.rate.Rate;
import fr.ymanvieu.trading.common.rate.event.RatesUpdatedEvent;

@Component
public class RatesStompHandler {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@EventListener
	public void send(RatesUpdatedEvent event) {
		messagingTemplate.convertAndSend("/topic/latest/", event.getRates());
		
		for (Rate rate : event.getRates()) {
			messagingTemplate.convertAndSend("/topic/latest/" + rate.getFromcur().getCode() + "/" + rate.getTocur().getCode(), rate);
		}
	}
}
