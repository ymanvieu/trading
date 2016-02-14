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
package fr.ymanvieu.forex.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.client.standard.WebSocketContainerFactoryBean;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import fr.ymanvieu.forex.core.websocket.RatesWebSocketHandler;
/**
 * http://docs.spring.io/spring/docs/current/spring-framework-reference/html/websocket.html#websocket-server-runtime-configuration </br>
 * http://docs.spring.io/spring-integration/reference/html/web-sockets.html </br>
 * https://github.com/salmar/spring-websocket-chat
 */
public class SockJsClientTest {

	private static final String URL = "ws://localhost:8080/latest";

	public static void main(String[] args) throws Exception {

		RatesWebSocketHandler eh = new RatesWebSocketHandler();
		
		//startWebSocket(eh);
		startSockJs(eh);

		synchronized (Thread.currentThread()) {
			Thread.currentThread().wait();
		}
	}

	private static void startSockJs(RatesWebSocketHandler eh) throws Exception {
		List<Transport> transports = new ArrayList<>(2);
		transports.add(new WebSocketTransport(createWebSocketClient()));
		transports.add(new RestTemplateXhrTransport());

		SockJsClient sockJsClient = new SockJsClient(transports);

		sockJsClient.doHandshake(eh, URL).get();
	}

	private static void startWebSocket(RatesWebSocketHandler eh) throws Exception {
		StandardWebSocketClient wsClient = createWebSocketClient();
		wsClient.doHandshake(eh, URL).get();
	}

	private static StandardWebSocketClient createWebSocketClient() throws Exception {
		return new StandardWebSocketClient(createWebSocketContainer().getObject());
	}

	private static WebSocketContainerFactoryBean createWebSocketContainer() {
		WebSocketContainerFactoryBean container = new WebSocketContainerFactoryBean();
		container.setMaxTextMessageBufferSize(128 * 1024);
		container.setMaxBinaryMessageBufferSize(128 * 1024);
		return container;
	}
}