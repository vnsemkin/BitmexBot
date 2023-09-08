package bitmex.bitmexspring.model.bitmex;

import bitmex.bitmexspring.model.user.BitmexData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WSAuth implements BitmexData {
    private String op = "authKeyExpires";
    private List<?> args;

    public WSAuth(List<?> args) {
        this.args = args;
    }
}
