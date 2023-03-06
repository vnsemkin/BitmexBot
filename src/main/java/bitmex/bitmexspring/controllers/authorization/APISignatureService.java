package bitmex.bitmexspring.controllers.authorization;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

public class APISignatureService {
    public String getAPISignature(String apiSecret, String verb, String path, long expires, String data) {
        return hmacWithApacheCommons(verb + path + expires + data, apiSecret);
    }

    public String getAPISignatureForWS(String apiSecret, long expires) {
        return hmacWithApacheCommons("GET/realtime" + expires, apiSecret);
    }


    private static String hmacWithApacheCommons(String data, String key) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmacHex(data);
    }
}
