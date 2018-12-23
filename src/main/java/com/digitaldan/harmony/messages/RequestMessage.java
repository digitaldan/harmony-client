package com.digitaldan.harmony.messages;

import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;

public abstract class RequestMessage {
    String mimeType;
    String id;
    Gson gson = new Gson();

    public RequestMessage(String mimeType) {
        this.mimeType = mimeType;
        this.id = UUID.randomUUID().toString();
    }

    public String toJson() {
        return gson.toJson(new JsonRequest(mimeType, id, getParams()));
    }

    public String getId() {
        return id;
    }

    public abstract Map<String, Object> getParams();

    /**
     * Private class for JSON serialization.
     */
    private class JsonRequest {
        HBus hbus;

        public JsonRequest(String mimeType, String id, Map<String, Object> params) {
            hbus = new HBus();
            hbus.cmd = mimeType;
            hbus.id = id;
            hbus.params = params;
        }

        private class HBus {
            @SuppressWarnings("unused")
            String cmd;
            @SuppressWarnings("unused")
            String id;
            @SuppressWarnings("unused")
            Map<String, Object> params;
        }
    }
}
