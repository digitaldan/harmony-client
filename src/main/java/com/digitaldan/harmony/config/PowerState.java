package com.digitaldan.harmony.config;

import java.util.HashMap;
import java.util.Map;

public enum PowerState {
    ON("On"),
    OFF("Off");

    private static Map<String, PowerState> valueMap;

    private final String description;

    private PowerState(String description) {
        this.description = description;
        storeInValueMap(this);
    }

    private void storeInValueMap(PowerState PowerState) {
        if (valueMap == null) {
            valueMap = new HashMap<String, PowerState>();
        }
        valueMap.put(description, this);
    }

    // @SerializedName()
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }

    // @JsonCreator
    public static PowerState forValue(String description) {
        PowerState result = valueMap.get(description);
        if (result != null) {
            return result;
        }
        return valueOf(description);
    }
}
