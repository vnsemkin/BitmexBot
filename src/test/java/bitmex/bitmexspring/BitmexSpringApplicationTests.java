package bitmex.bitmexspring;

import bitmex.bitmexspring.service.WSHandler;
import bitmex.bitmexspring.util.authorization.APISignatureService;
import bitmex.bitmexspring.util.json.JsonParser;
import bitmex.bitmexspring.model.user.BitmexData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import bitmex.bitmexspring.model.user.User;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = {JsonParser.class, WSHandler.class})
@PropertySource("classpath:network.properties")
class BitmexSpringApplicationTests {
    @Value("${app.symbol}")
    String symbol;
    JsonParser json;
    WSHandler wsHandler;
    String testKey = "YeOJnM7jXJKV8pf5dYMalLs0";
    String testSecret = "He6LhcpmH9oKNqjYu2RaxqsoutyGf2-0VUVPbIdiGCKOx_j2";

    @Autowired
    public BitmexSpringApplicationTests(JsonParser json, WSHandler wsHandler) {
        this.json = json;
        this.wsHandler = wsHandler;
    }

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
        assertThat(userExpected)
                .isEqualTo(new User("string", "string"));
        assertThat(userExpected.getUserName()).isEqualTo("string");
        assertThat(userExpected.getEmail()).isEqualTo("string");
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
        assertThat(bitmexData)
                .isEqualTo(new User("string", "string"));
        User user = (User) bitmexData;
        assertThat(user.getUserName()).isEqualTo("string");
        assertThat(user.getEmail()).isEqualTo("string");
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