package bitmexbot.util.json;

import bitmexbot.model.user.BitmexData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

@Component
public class JsonParser {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BitmexData readToObject(String body, Class<? extends BitmexData> dataClass) {
        BitmexData bitmexData = null;
        try {
            bitmexData = objectMapper.readValue(body, dataClass);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return bitmexData;
    }

    public BitmexData readToObject(File file, Class<? extends BitmexData> dataClass) {
        BitmexData bitmexData = null;
        try {
            bitmexData = objectMapper.readValue(file, dataClass);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bitmexData;
    }


    public String writeToString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTable(String jsonData) {
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(jsonData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (Objects.nonNull(jsonNode)) {
            Iterator<String> iterator = jsonNode.fieldNames();
            String field = iterator.next();
            return field.equals("table");
        }
        return false;
    }
}


