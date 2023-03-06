package bitmex.bitmexspring.controllers.json;

import bitmex.bitmexspring.models.user.BitmexData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;

@Controller
public class JsonController  {
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


    public String writeToString(BitmexData bitmexData)  {
        try {

            return objectMapper.writeValueAsString(bitmexData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}


