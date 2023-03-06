package bitmex.bitmexspring.controllers.authorization;

import bitmex.bitmexspring.models.bitmex.APIAuthData;
import bitmex.bitmexspring.models.bitmex.ClientData;

public class APIAuthDataService {

    public APIAuthData getAPIAutData(ClientData clientData,
                                     String httpMethod,
                                     String path,
                                     String data) {
        long apiExpires = getExpires();
        String apiSignature = new APISignatureService().getAPISignature(clientData.getSecret(), httpMethod, path, apiExpires, data);
        return new APIAuthData(clientData.getKey(), apiExpires, apiSignature);
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
