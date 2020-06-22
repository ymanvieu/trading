/**
 * Copyright (C) 2017 Yoann Manvieu
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
package fr.ymanvieu.trading.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ymanvieu.trading.common.rate.event.RatesUpdatedEvent;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class JmsConfig {

	@Autowired
	private ApplicationEventPublisher bus;

	@Bean // Serialize message content to json using TextMessage
	public MessageConverter jacksonJmsMessageConverter(ObjectMapper mapper) {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setObjectMapper(mapper);
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}
	
	@Bean
	public JmsListenerContainerFactory<?> myFactory(DefaultJmsListenerContainerFactory factory) {
		// DefaultJmsListenerContainerFactory provides all boot's default to this factory, including the message converter
		// You could still override some of Boot's default if necessary.
		factory.setClientId("trading-webapp");
		factory.setSubscriptionDurable(true);
		return factory;
	}

	@JmsListener(destination = "trading.rate.latest?consumer.retroactive=true")
	public void receiveMessage(RatesUpdatedEvent event) {
		log.debug("Received: {}", event);
		
		bus.publishEvent(event);
	}
}