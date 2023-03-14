package bitmex.bitmexspring.controllers.web;

import feign.Client;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
@PropertySource("classpath:network.properties")
public class FeignClientConf {
    @Value("${proxy.host}")
    String proxyHost;
    @Value("${proxy.port}")
    String proxyPort;

//    @Bean
//    public Client feignClient() {
//        return new Client.Proxied(null, null,
//                new Proxy(Proxy.Type.HTTP,
//                        new InetSocketAddress( proxyHost, Integer.parseInt(proxyPort))));
//    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Content-Type", "application/json");
            requestTemplate.header("Accept", "application/json");
        };
    }
}
