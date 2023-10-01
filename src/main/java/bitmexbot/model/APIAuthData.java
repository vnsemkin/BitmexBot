package bitmexbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIAuthData {
    private String apiKey;
    private long apiExpires;
    private String apiSignature;

}
