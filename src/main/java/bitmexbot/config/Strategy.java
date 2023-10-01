package bitmexbot.config;

import java.util.HashMap;
import java.util.Map;

public enum Strategy {
    LINEAR_BUY_AND_SELL;

    private static final Map<Strategy, String> strategyLabels = new HashMap<>();

    static {
        strategyLabels.put(LINEAR_BUY_AND_SELL, "Линейная покупка и продажа");
    }

    public static String getLabel(Strategy strategy) {

        return strategyLabels.get(strategy);
    }
}
