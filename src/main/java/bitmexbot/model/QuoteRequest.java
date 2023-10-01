package bitmexbot.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuoteRequest {
    @JsonProperty("symbol")
    String symbol;
    @JsonProperty("count")
    double count;
    @JsonProperty("reverse")
    boolean reverse;
}
