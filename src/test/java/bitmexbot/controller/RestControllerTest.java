package bitmexbot.controller;

import bitmexbot.dto.BotDTO;
import bitmexbot.network.FeignClientTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@Sql("/script.sql")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RestControllerTest {
    @Autowired
    FeignClientTest feignClientTest;

    @Test
    public void shouldReturnBotOnGet(){
        ResponseEntity<List<BotDTO>> botRespEntity = feignClientTest.getBotList();
        Assertions.assertEquals(HttpStatus.OK, botRespEntity.getStatusCode());
    }
}
