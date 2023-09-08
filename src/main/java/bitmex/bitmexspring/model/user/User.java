package bitmex.bitmexspring.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements BitmexData {
    @JsonProperty("username")
    private String userName;
    @JsonProperty("email")
    private String email;
}
