package bitmex.bitmexspring.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWallet implements BitmexData {
    @JsonProperty("account")
    private int account;
    @JsonProperty("currency")
    private String currency;
}
