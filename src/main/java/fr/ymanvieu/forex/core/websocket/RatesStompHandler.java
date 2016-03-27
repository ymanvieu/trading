/**
 * Copyright (C) 2015 Yoann Manvieu
 *
 * This software is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ymanvieu.forex.core.websocket;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import fr.ymanvieu.forex.core.event.RatesUpdatedEvent;
import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;

@ConditionalOnWebApplication
@Controller
public class RatesStompHandler {

	private static final Logger LOG = LoggerFactory.getLogger(RatesStompHandler.class);

	private final SimpMessagingTemplate messagingTemplate;

	private final EventBus bus;

	@Autowired
	public RatesStompHandler(SimpMessagingTemplate messagingTemplate, EventBus bus) {
		this.messagingTemplate = messagingTemplate;
		this.bus = bus;
	}

	@PostConstruct
	private void registerToBus() {
		bus.register(this);
	}

	@MessageMapping("/hello")
	@SendToUser("/queue/greetings")
	public String greeting(String payload) {
		LOG.info("{}", payload);
		return System.currentTimeMillis() + " : " + payload;
	}

	@Subscribe
	public void send(RatesUpdatedEvent event) {
		for (RateEntity re : event.getRates()) {
			messagingTemplate.convertAndSend("/topic/latest/" + re.getFromcur().getCode() + "/" + re.getTocur().getCode(), re);
		}
	}
}