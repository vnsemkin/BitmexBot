package bitmexbot.config;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StrategyConverter implements Converter<String, Strategy> {

    @Override
    public Strategy convert(@NonNull String source) {
        // Implement the logic to convert the string to the Strategy enum.
        for (Strategy strategy : Strategy.values()) {
            if (Strategy.getLabel(strategy).equals(source)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("Invalid Strategy: " + source);
    }
}

