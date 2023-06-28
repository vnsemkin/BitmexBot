package bitmex.bitmexspring.models.bitmex;

import bitmex.bitmexspring.models.user.BitmexData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WSRequest implements BitmexData {
    private String op;
    private List<?> args;
}
