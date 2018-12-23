package com.digitaldan.harmony.messages;

import java.util.HashMap;
import java.util.Map;

import com.digitaldan.harmony.config.HarmonyConfig;
import com.google.gson.annotations.SerializedName;

public class ConfigMessage {
    public static final String MIME_TYPE = "vnd.logitech.harmony/vnd.logitech.harmony.engine?config";

    public static class ConfigRequestMessage extends RequestMessage {

        public ConfigRequestMessage() {
            super(MIME_TYPE);
        }

        @Override
        public Map<String, Object> getParams() {
            return new HashMap<>();
        }

    }

    public static class ConfigResponseMessage extends ResponseMessage {

        @SerializedName("data")
        private HarmonyConfig harmonyConfig;

        public ConfigResponseMessage(int code, String id, String msg, HarmonyConfig harmonyConfig) {
            super(code, id, msg);
            this.harmonyConfig = harmonyConfig;
        }

        public HarmonyConfig getHarmonyConfig() {
            return harmonyConfig;
        }

    }

    // @Override
    // public ConfigResponseMessage fromJsonElement(int code, String id, String msg, JsonElement data) {
    // Gson gson = new Gson();
    // return new ConfigResponseMessage(code, id, msg, gson.fromJson(data, HarmonyConfig.class));
    // }
}
