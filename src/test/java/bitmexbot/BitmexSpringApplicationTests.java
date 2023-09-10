package bitmexbot;

import bitmexbot.model.user.BitmexData;
import bitmexbot.model.user.User;
import bitmexbot.service.WSHandler;
import bitmexbot.util.authorization.APISignatureService;
import bitmexbot.util.json.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@PropertySource("classpath:network.properties")
class BitmexSpringApplicationTests {
    @Value("${app.symbol}")
    String symbol;
    @Autowired
    JsonParser json;
    @Autowired
    WSHandler wsHandler;
    String testKey = "YeOJnM7jXJKV8pf5dYMalLs0";
    String testSecret = "He6LhcpmH9oKNqjYu2RaxqsoutyGf2-0VUVPbIdiGCKOx_j2";


    @Test
    public void APISignatureGenerator() {
        //GIVEN
        APISignatureService apiSignatureGenerator = new APISignatureService();
        String apiSecret = "chNOOS4KvNXR_Xq4k4c9qsfoKWvnDecLATCRlcBwyKDYnWgO";

        String path = "/api/v1/instrument";
        String path1 = "/api/v1/instrument?filter=%7B%22symbol%22%3A+%22XBTM15%22%7D";
        long expires = 1518064236;
        long expires1 = 1518064237;
        String data = "";
        //WHEN
        String expected = "c7682d435d0cfe87c16098df34ef2eb5a549d4c5a3c2b1f0f77b8af73423bf00";
        String expected1 = "e2f422547eecb5b3cb29ade2127e21b858b235b386bfa45e1c1756eb3383919f";
        String actual = apiSignatureGenerator.getAPISignature(apiSecret, String.valueOf(HttpMethod.GET), path, expires, data);
        String actual1 = apiSignatureGenerator.getAPISignature(apiSecret, String.valueOf(HttpMethod.GET), path1, expires1, data);
        //THEN
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(expected1, actual1);
    }

    @Test
    public void jsonReadToObjectFromFile() throws IOException {
        //GIVEN

        User userExpected = (User) json.readToObject(new File("src/test/java/bitmex/bitmexspring/model/user.json"), User.class);
        //WHEN
        //THEN
        assertEquals(userExpected,(new User("string", "string")));
        assertEquals(userExpected.getUserName(),("string"));
        assertEquals(userExpected.getEmail(),("string"));
    }

    @Test
    public void jsonReadToObjectFromString() throws IOException {
        //GIVEN
        String jsonData = "{\n" +
                "  \"id\": 0,\n" +
                "  \"ownerId\": 0,\n" +
                "  \"firstname\": \"string\",\n" +
                "  \"username\": \"string\",\n" +
                "  \"email\": \"string\"}";
        BitmexData bitmexData = json.readToObject(jsonData, User.class);
        //WHEN
        //THEN
        assertEquals(bitmexData, (new User("string", "string")));
        User user = (User) bitmexData;
        assertEquals(user.getUserName(),("string"));
        assertEquals(user.getEmail(),("string"));
    }


    @Test
    public void createJsonFromObject() {
        //GIVEN
        User user = new User("Ivan", "ivan@gmail.com");
        //WHEN
        String jsonExpected = "{" +
                "\"username\":\"Ivan\"" + "," +
                "\"email\":\"ivan@gmail.com\"}";
        String jsonActual = json.writeToString(user);
        //THEN
        Assertions.assertEquals(jsonExpected, jsonActual);

    }
}