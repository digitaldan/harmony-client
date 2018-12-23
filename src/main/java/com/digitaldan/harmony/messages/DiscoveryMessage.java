package com.digitaldan.harmony.messages;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.digitaldan.harmony.config.Discovery;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class DiscoveryMessage {

    public static String MIME_TYPE = "connect.discoveryinfo?get";

    public static class DiscoveryRequestMessage {
        @SuppressWarnings("unused")
        String cmd = MIME_TYPE;
        @SuppressWarnings("unused")
        String id = UUID.randomUUID().toString();
        @SuppressWarnings("unused")
        Map<String, Object> params = new HashMap<>();

        public DiscoveryRequestMessage() {

        }

        public String toJSON() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }

    public static class DiscoveryResponseMessage {

        @SerializedName("data")
        private Discovery discovery;
        String id;
        String msg;

        public DiscoveryResponseMessage() {
        }

        public Discovery getDiscovery() {
            return discovery;
        }

        public static DiscoveryResponseMessage fromJSON(String json) {
            Gson gson = new Gson();
            return gson.fromJson(json, DiscoveryResponseMessage.class);
        }
    }

}
