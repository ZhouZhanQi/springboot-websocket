package com.zhouzq.cloud.microservicea.config;

import com.zhouzq.cloud.microservicea.factory.AuthWebSocketHandlerDecoratorFactory;
import com.zhouzq.cloud.microservicea.helper.WebSocketHandshakeHandler;
import com.zhouzq.cloud.microservicea.helper.WebSocketHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * @author zhouzq
 * @date 2019/5/24
 * @desc
 */
@Configuration
// 此注解开使用STOMP协议来传输基于消息代理的消息，此时可以在@Controller类中使用@MessageMapping
@EnableWebSocketMessageBroker
public class WebSocketRabbitMQMessageBrokerConfigurer implements WebSocketMessageBrokerConfigurer {


    @Autowired
    private AuthWebSocketHandlerDecoratorFactory myWebSocketHandlerDecoratorFactory;

    @Autowired
    private WebSocketHandshakeHandler webSocketHandshakeHandler;

    @Autowired
    private WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /**
         * 注册 Stomp的端点
         *
         * addEndpoint：添加STOMP协议的端点。这个HTTP URL是供WebSocket或SockJS客户端访问的地址
         * withSockJS：指定端点使用SockJS协议
         */
        registry.addEndpoint("/ws/icc/websocket")
                .addInterceptors(webSocketHandshakeInterceptor)
                .setHandshakeHandler(webSocketHandshakeHandler)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        /**
         * 配置消息代理
         * 使用RabbitMQ做为消息代理，替换默认的Simple Broker
         */
        registry
                .setApplicationDestinationPrefixes("/mq") // 指服务端接收地址的前缀，意思就是说客户端给服务端发消息的地址的前缀
                // "STOMP broker relay"处理所有消息将消息发送到外部的消息代理
                .enableStompBrokerRelay("/exchange","/topic","/queue","/amq/queue")
                .setVirtualHost("/icc-local")
                //.setRelayHost("192.168.0.113")
                .setRelayHost("192.168.21.3")
                .setClientLogin("icc-dev")
                .setClientPasscode("icc-dev")
                .setSystemLogin("icc-dev")
                .setSystemPasscode("icc-dev")
                .setSystemHeartbeatSendInterval(5000)
                .setSystemHeartbeatReceiveInterval(4000);
    }


    /**
     * 这时实际spring weboscket集群的新增的配置，用于获取建立websocket时获取对应的sessionid值
     * @param registration
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(myWebSocketHandlerDecoratorFactory);
    }

}
