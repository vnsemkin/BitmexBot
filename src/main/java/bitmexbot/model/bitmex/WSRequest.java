package bitmexbot.model.bitmex;

import bitmexbot.model.user.BitmexData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WSRequest implements BitmexData {
    private String op;
    private List<?> args;
}
