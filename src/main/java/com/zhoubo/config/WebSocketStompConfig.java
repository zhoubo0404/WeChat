package com.zhoubo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * Created by zhoubo on 2017/7/26.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint("/marcopolo").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        super.configureMessageBroker(registry);
//        registry.enableSimpleBroker("/queue", "/topic");
        registry.enableStompBrokerRelay("/queue", "/topic")
                .setRelayHost("127.0.0.1")
                .setRelayPort(61613);
/*        registry.enableStompBrokerRelay("/queue", "/topic")
                .setRelayPort(61614);*/
        registry.setApplicationDestinationPrefixes("/app");
    }
}
