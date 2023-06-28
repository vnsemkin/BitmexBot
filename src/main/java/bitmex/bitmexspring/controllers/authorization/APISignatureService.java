package bitmex.bitmexspring.controllers.authorization;

import bitmex.bitmexspring.config.BitmexConstants;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

public class APISignatureService {
    public String getAPISignature(String apiSecret, String verb, String path, long expires, String data) {
        return hmacWithApacheCommons(verb + path + expires + data, apiSecret);
    }

    public String getAPISignatureForWS(String apiSecret, long expires) {
        return hmacWithApacheCommons(BitmexConstants.GET_REAL_TIME + expires, apiSecret);
    }

    private static String hmacWithApacheCommons(String data, String key) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmacHex(data);
    }
}
