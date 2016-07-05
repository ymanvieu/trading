/**
 * Copyright (C) 2016 Yoann Manvieu
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
package fr.ymanvieu.trading;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import fr.ymanvieu.trading.rate.entity.RateEntity;

public class StompClientTest {

	private static final Logger LOG = LoggerFactory.getLogger(StompClientTest.class);

	private static final String URL = "ws://localhost:8080/stomp";

	public static void main(String[] args) throws Exception {
		start();

		synchronized (Thread.currentThread()) {
			Thread.currentThread().wait();
		}
	}

	private static void start() throws Exception {
		List<Transport> transports = Collections.<Transport> singletonList(new WebSocketTransport(new StandardWebSocketClient()));
		WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(transports));

		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		StompSessionHandlerAdapter handler = new StompSessionHandlerAdapter() {
			@Override
			public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
				LOG.info("Connected to {}", session);
			}

			@Override
			public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
				throw new RuntimeException(exception);
			}
		};

		ListenableFuture<StompSession> lss = stompClient.connect(URL, handler);

		StompSession ss = lss.get();

		ss.subscribe("/user/queue/greetings", new StringHandler());
		ss.subscribe("/topic/latest/USD/EUR", new RatesHandler());
		ss.subscribe("/topic/latest/BRE/USD", new RatesHandler());

		ss.send("/app/hello", "dede");

		LOG.info("hello done");
	}

	public static class RatesHandler extends StompSessionHandlerAdapter {

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			LOG.info("{}", payload);
		}

		@Override
		public Type getPayloadType(StompHeaders headers) {
			return RateEntity.class;
		}
	}

	public static class StringHandler extends StompSessionHandlerAdapter {

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			LOG.info("{}", payload);
		}
	}
}