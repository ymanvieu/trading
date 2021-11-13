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
package fr.ymanvieu.trading.datacollect.config;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.broker.region.policy.SimpleDispatchPolicy;
import org.apache.activemq.broker.region.policy.TimedSubscriptionRecoveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ActiveMQConfig {

	@ConditionalOnProperty("spring.activemq.broker-url")
	@Bean(initMethod = "start", destroyMethod = "stop")
	public BrokerService broker(@Value("${spring.activemq.broker-url}") String brokerUrl) throws Exception {
		PolicyEntry policy = new PolicyEntry();
		policy.setTopic(">");
		policy.setDispatchPolicy(new SimpleDispatchPolicy());
		
		TimedSubscriptionRecoveryPolicy recoveryPolicy = new TimedSubscriptionRecoveryPolicy();
		recoveryPolicy.setRecoverDuration(24 * 60 * 60 * 1000); // 1 day
		
		policy.setSubscriptionRecoveryPolicy(recoveryPolicy);
		PolicyMap policyMap = new PolicyMap();
		policyMap.setDefaultEntry(policy);

		BrokerService broker = new BrokerService();
		broker.addConnector(brokerUrl);
		broker.setPersistent(false);
		broker.setUseJmx(false);
		broker.setDestinationPolicy(policyMap);
		
		return broker;
	}

	@Bean // Serialize message content to json using TextMessage
	public MessageConverter jacksonJmsMessageConverter(ObjectMapper mapper) {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setObjectMapper(mapper);
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}
}