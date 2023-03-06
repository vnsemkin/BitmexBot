package bitmex.bitmexspring.trash;

import bitmex.bitmexspring.model.bitmex.APIAuthData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

@Controller//this is Spring bean end point
@PropertySource("network.properties")//where get data from properties
public class WebClientController {
    @Value("${proxy.host}")//field proxy.host
    private String PROXY_HOST;
    @Value("${proxy.port}")//field proxy.port
    private String PROXY_PORT;
    @Value("${application.url}")//field application url
    private String URL;

    public String request(String endPoint, APIAuthData authData, String method, String data, boolean proxy) {
        if (proxy) {
            setProxy();
        }
        final WebClient webClient = WebClient
                .builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("api-expires", String.valueOf(authData.apiExpires()))
                .defaultHeader("api-key", authData.apikey())
                .defaultHeader("api-signature", authData.apiSignature())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().proxyWithSystemProperties()))
                .build();

        return webClient.method(HttpMethod.valueOf(method))
                .uri(URL + endPoint)
                .body(BodyInserters.fromValue(data))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String request(String endPoint, APIAuthData authData, String method
            ,String headerName,String headerValue, String data, boolean proxy) {
        if (proxy) {
            setProxy();
        }
        final WebClient webClient = WebClient
                .builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("api-expires", String.valueOf(authData.apiExpires()))
                .defaultHeader("api-key", authData.apikey())
                .defaultHeader("api-signature", authData.apiSignature())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().proxyWithSystemProperties()))
                .build();

        return webClient.method(HttpMethod.valueOf(method))
                .uri(URL + endPoint)
                .header(headerName,headerValue)
                .body(BodyInserters.fromValue(data))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private void setProxy() {
        System.setProperty("http.proxyHost", PROXY_HOST);
        System.setProperty("http.proxyPort", PROXY_PORT);
    }
}

//            String userNameResponse = webClientController.request(BitmexEndpoints.USER,
//                    getAuthorization(BitmexEndpoints.USER, HttpMethod.GET, emptyData),
//                    String.valueOf(HttpMethod.GET), emptyData, true);
//            String userWalletResponse = webClientController.request(BitmexEndpoints.USER_WALLET,
//                    getAuthorization(BitmexEndpoints.USER_WALLET, HttpMethod.GET, emptyData),
//                    String.valueOf(HttpMethod.GET), emptyData, false);

//            User user = (User) json.readToObject(userNameResponse, User.class);
//            UserWallet userWallet = (UserWallet) json.readToObject(userWalletResponse, UserWallet.class);
