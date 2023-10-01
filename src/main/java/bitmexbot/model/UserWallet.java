package bitmexbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserWallet implements BitmexData {
    @JsonProperty("account")
    private int account;
    @JsonProperty("currency")
    private String currency;

}
