package de.hska.lkit.demo.redis.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import de.hska.lkit.demo.redis.configuration.SimpleWebSocketsProperties;

@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(SimpleWebSocketsProperties.class)
public class WebSocketsConfig extends AbstractWebSocketMessageBrokerConfigurer {
	
	SimpleWebSocketsProperties props;
	
	public WebSocketsConfig(SimpleWebSocketsProperties props){
		this.props = props;
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		//For the same domain
		registry.addEndpoint(props.getEndpoint()).withSockJS();
		
		//Necessary if there is a cross-domain
		//registry.addEndpoint(props.getEndpoint()).setAllowedOrigins("*").withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		//Application Prefix:
		config.setApplicationDestinationPrefixes(props.getAppDestinationPrefix());
		
		//Enable the following for a simple broker
		config.enableSimpleBroker(props.getTopic());
		
		//Enable the following for RabbitMQ Broker relay. If enable, then comment out the above statement:  enableSimpleBroker
		//config.enableStompBrokerRelay("/topic", "/queue").setRelayPort(61613); // This is the RabbitMQ stomp port
		
	}
}

