package bitmexbot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuoteResponse {
    @JsonProperty("symbol")
    String symbol;
    @JsonProperty("bidPrice")
    int lastBid;
    @JsonProperty("askPrice")
    int lastAsk;
}
