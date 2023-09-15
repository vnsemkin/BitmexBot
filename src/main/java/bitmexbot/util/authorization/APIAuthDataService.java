package bitmexbot.util.authorization;

import bitmexbot.entity.BitmexBotData;
import bitmexbot.model.APIAuthData;

public class APIAuthDataService {

    public APIAuthData getAPIAutData(BitmexBotData bitmexBotData,
                                     String httpMethod,
                                     String path,
                                     String data) {
        long apiExpires = getExpires();
        String apiSignature = new APISignatureService().getAPISignature(bitmexBotData.getSecret(), httpMethod, path, apiExpires, data);
        return new APIAuthData(bitmexBotData.getKey(), apiExpires, apiSignature);
    }

    public APIAuthData getAPIAutDataWS(String apiKey,
                                       String apiSecret) {
        long apiExpires = getExpires();
        String apiSignature = new APISignatureService().getAPISignatureForWS(apiSecret, apiExpires);
        return new APIAuthData(apiKey, apiExpires, apiSignature);
    }


    private long getExpires() {
        return System.currentTimeMillis() / 1000 + 60;
    }
}
