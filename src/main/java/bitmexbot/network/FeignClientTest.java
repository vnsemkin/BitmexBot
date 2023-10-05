package bitmexbot.network;

import bitmexbot.dto.BotDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "testFeignClient", url = "http://localhost:8080/rest/")
public interface FeignClientTest {

    @GetMapping("/bot")
    ResponseEntity<List<BotDTO>> getBotList();
}
