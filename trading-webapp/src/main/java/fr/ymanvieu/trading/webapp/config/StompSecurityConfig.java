package fr.ymanvieu.trading.webapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
@Order(1)
public class StompSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

	@Override
	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
		messages
			.nullDestMatcher().permitAll()
			.simpSubscribeDestMatchers("/topic/latest/**").permitAll()
			.anyMessage().denyAll();
	}

	@Override
	protected boolean sameOriginDisabled() {
		return true;
	}
}
