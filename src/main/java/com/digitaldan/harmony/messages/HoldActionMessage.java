package com.digitaldan.harmony.messages;

import java.util.HashMap;
import java.util.Map;

public class HoldActionMessage {

    public static final String MIME_TYPE = "vnd.logitech.harmony/vnd.logitech.harmony.engine?holdAction";

    public enum HoldStatus {
        PRESS,
        RELEASE
    }

    public static class HoldActionRequestMessage extends RequestMessage {

        HashMap<String, Object> params = new HashMap<>();

        public HoldActionRequestMessage(int deviceId, String button, HoldStatus status, long timestamp) {
            super(MIME_TYPE);
            HashMap<String, Object> action = new HashMap<>();
            action.put("type", "IRCommand");
            action.put("deviceId", String.valueOf(deviceId));
            action.put("command", button);

            params.put("action", action);
            params.put("status", status.toString().toLowerCase());
            params.put("timestamp", timestamp);
        }

        @Override
        public Map<String, Object> getParams() {
            return params;
        }

    }

    public static class HoldActionResponseMessage extends ResponseMessage {
        public HoldActionResponseMessage(int code, String id, String msg) {
            super(code, id, msg);
        }
    }
}
