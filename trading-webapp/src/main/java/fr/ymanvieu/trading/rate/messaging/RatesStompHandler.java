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
package fr.ymanvieu.trading.rate.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import fr.ymanvieu.trading.rate.entity.RateEntity;
import fr.ymanvieu.trading.rate.event.RatesUpdatedEvent;

@Component
public class RatesStompHandler {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@EventListener
	public void send(RatesUpdatedEvent event) {
		messagingTemplate.convertAndSend("/topic/latest/", event.getRates());
		
		for (RateEntity re : event.getRates()) {
			messagingTemplate.convertAndSend("/topic/latest/" + re.getFromcur().getCode() + "/" + re.getTocur().getCode(), re);
		}
	}
}