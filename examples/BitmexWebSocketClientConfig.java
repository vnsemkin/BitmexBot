package bitmex.bitmexspring.trash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class BitmexWebSocketClientConfig {

    private static final String BITMEX_REALTIME_ENDPOINT = "wss://ws.bitmex.com/realtime";
    private static final String BITMEX_TOPIC = "announcement";

    @Bean
    public WebSocketStompClient webSocketStompClient() {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.setTaskScheduler(new ConcurrentTaskScheduler());
        return stompClient;
    }

    @Bean
    public StompSessionHandler bitmexStompSessionHandler() {
        return new BitmexStompSessionHandler();
    }

    @Bean
    public WebSocketConnectionManager webSocketConnectionManager() {
        WebSocketConnectionManager connectionManager = new WebSocketConnectionManager((WebSocketClient) webSocketStompClient(),
                (WebSocketHandler) bitmexStompSessionHandler(),
                BITMEX_REALTIME_ENDPOINT);
        connectionManager.setAutoStartup(true);
        return connectionManager;
    }

    private static class BitmexStompSessionHandler extends StompSessionHandlerAdapter {

        private static final Logger LOGGER = LoggerFactory.getLogger(BitmexStompSessionHandler.class);

//        @Override
//        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//            LOGGER.info("Connected to Bitmex WebSocket endpoint: {}", BITMEX_REALTIME_ENDPOINT);
//            session.subscribe(BITMEX_TOPIC, new StompFrameHandler() {
//                @Override
//                public OrderBook getPayloadType(StompHeaders headers) {
//                    return OrderBook.class;
//                }
//
//                @Override
//                public void handleFrame(StompHeaders headers, Object payload) {
//                    OrderBook orderBook = (OrderBook) payload;
//                    LOGGER.info("Received order book update: {}", orderBook);
//                }
//            });
//        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            LOGGER.error("Error occurred in Bitmex WebSocket transport", exception);
        }
    }
}