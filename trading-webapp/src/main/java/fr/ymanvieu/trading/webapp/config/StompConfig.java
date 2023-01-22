package fr.ymanvieu.trading.webapp.config;

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
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import fr.ymanvieu.trading.webapp.jwt.JwtTokenUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnWebApplication
@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    @Configuration
    public static class WebSocketMessageBrokerStatsConfig {

        @Autowired
        private WebSocketMessageBrokerStats webSocketMessageBrokerStats;

        @PostConstruct
        public void init() {
            webSocketMessageBrokerStats.setLoggingPeriod(-1); // disable log of stats
        }
    }
	
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
		registry.addEndpoint("/stomp").setAllowedOrigins("*").withSockJS().setSessionCookieNeeded(false).setSuppressCors(true);
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

			User userDetails = new User(username, "", jwtTokenUtil.getGrantedAuthoritiesFromToken(authToken));

			if (jwtTokenUtil.validateToken(authToken, userDetails)) {
            	return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			}
		}
		
		return null;
	}
}
