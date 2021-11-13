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
package fr.ymanvieu.trading.webapp.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import fr.ymanvieu.trading.webapp.jwt.JwtTokenUtil;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnWebApplication
@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class StompConfig implements WebSocketMessageBrokerConfigurer {
	
	@Autowired
	private WebSocketMessageBrokerStats webSocketMessageBrokerStats;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Value("${jwt.header}")
	private String tokenHeader;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic", "/queue");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// https://docs.spring.io/spring-framework/docs/4.2.4.RELEASE/spring-framework-reference/html/websocket.html#websocket-fallback-cors
		registry.addEndpoint("/stomp").setAllowedOrigins("*").withSockJS().setSessionCookieNeeded(false).setSupressCors(true);
	}

	@PostConstruct
	public void init() {
	    webSocketMessageBrokerStats.setLoggingPeriod(-1); // disable log of stats
	}
	
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					String bearer = accessor.getFirstNativeHeader(tokenHeader);
					log.debug("StompHeader {}: {}", tokenHeader, bearer);
					Authentication user = getUser(bearer);
					accessor.setUser(user);
				}
				return message;
			}
		});
	}
	
	private Authentication getUser(String authToken) {
		if(authToken == null) {
			return null;
		}

		String username = null;
		try {
			username = jwtTokenUtil.getSubjectFromToken(authToken);
		} catch (IllegalArgumentException e) {
			log.error("an error occured during getting username from token", e);
		} catch (JwtException e) {
			log.info("Security exception - {}", e.getMessage());
		}


		if (username != null) {
			log.debug("checking authentication for user " + username);

			// It is not compelling necessary to load the user details from the database.
			// You could also store the information in the token and read it from it. It's up to you ;)
			User userDetails = new User(username, "", jwtTokenUtil.getGrantedAuthoritiesFromToken(authToken));

			// For simple validation it is completely sufficient to just check the token integrity. You don't have to call
			// the database compellingly. Again it's up to you ;)
			if (jwtTokenUtil.validateToken(authToken, userDetails)) {
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());
				
				return authentication;
			}
		}
		
		return null;
	}
}