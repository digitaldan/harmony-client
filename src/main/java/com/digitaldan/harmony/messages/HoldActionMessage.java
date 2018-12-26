package com.digitaldan.harmony.messages;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class HoldActionMessage {

    public static final String MIME_TYPE = "vnd.logitech.harmony/vnd.logitech.harmony.engine?holdAction";
    Gson gson = new Gson();

    public enum HoldStatus {
        PRESS,
        RELEASE
    }

    public static class HoldActionRequestMessage extends RequestMessage {

        HashMap<String, Object> params = new HashMap<>();

        public HoldActionRequestMessage(int deviceId, String button, HoldStatus status, long timestamp) {
            super(MIME_TYPE);
            HashMap<String, Object> action = new HashMap<>();
            action.put("command", button);
            action.put("type", "IRCommand");
            action.put("deviceId", String.valueOf(deviceId));

            params.put("action", gson.toJson(action));
            params.put("status", status.toString().toLowerCase());
            params.put("timestamp", timestamp);
        }

        @Override
        public Map<String, Object> getParams() {
            return params;
        }

    }
}
