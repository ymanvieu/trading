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
package fr.ymanvieu.trading.rate.websocket;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import fr.ymanvieu.trading.rate.entity.RateEntity;
import fr.ymanvieu.trading.rate.event.RatesUpdatedEvent;

@ConditionalOnWebApplication
public class RatesStompHandler {

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

	@Subscribe
	public void send(RatesUpdatedEvent event) {
		for (RateEntity re : event.getRates()) {
			messagingTemplate.convertAndSend("/topic/latest/" + re.getFromcur().getCode() + "/" + re.getTocur().getCode(), re);
		}
	}
}