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

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import fr.ymanvieu.trading.rate.entity.RateEntity;

public class StompClientTest {

	private static final Logger LOG = LoggerFactory.getLogger(StompClientTest.class);

	private static final String LOGIN_URL = "http://localhost:8080/user/login";
	private static final String STOMP_URL = "ws://localhost:8080/stomp";

	private static final RestTemplate tp = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

	public static void main(String[] args) throws Exception {
		start();

		synchronized (Thread.currentThread()) {
			Thread.currentThread().wait();
		}
	}

	private static void start() throws Exception {
		

		List<Transport> transports = asList(new WebSocketTransport(new StandardWebSocketClient()));
		WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(transports));

		stompClient.setMessageConverter(new CompositeMessageConverter(asList(new MappingJackson2MessageConverter(), new StringMessageConverter())));

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

		WebSocketHttpHeaders wsHeaders = null;
		
//		wsHeaders = new WebSocketHttpHeaders();
//		String cookie = getAuthenticationSessionCookie();
//		LOG.info("Cookie: {}", cookie);
//		wsHeaders.set(HttpHeaders.COOKIE, cookie);

		ListenableFuture<StompSession> lss = stompClient.connect(STOMP_URL, wsHeaders, handler);

		StompSession ss = lss.get();
		
		RatesHandler rh = new RatesHandler();

		ss.subscribe("/topic/latest/USD/EUR", rh);
		ss.subscribe("/topic/latest/BRE/USD", rh);
	}

	public static String getAuthenticationSessionCookie() {
		ResponseEntity<String> response = tp.getForEntity(LOGIN_URL, String.class);

		Pattern p = Pattern.compile(".*name=\"_csrf\" value=\"([\\w-]*)\".*", Pattern.DOTALL);
		Matcher m = p.matcher(response.getBody());

		m.matches();
		String csrf = m.group(1);

		LOG.info("{}", csrf);

		MultiValueMap<String, String> v = new LinkedMultiValueMap<>();
		v.add("username", "user");
		v.add("password", "password");
		v.add("_csrf", csrf);

		response = tp.postForEntity(LOGIN_URL, v, String.class);		
		
		return response.getHeaders().get("Set-Cookie").get(0);
	}

	public static class RatesHandler extends StompSessionHandlerAdapter {

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			LOG.info("{}: {}", headers.getDestination(), payload);
		}

		@Override
		public Type getPayloadType(StompHeaders headers) {
			return RateEntity.class;
		}
	}
}