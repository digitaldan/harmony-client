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

        public HoldActionRequestMessage(int deviceId, String button, HoldStatus status) {
            super(MIME_TYPE);
            params.put("deviceId", deviceId);
            params.put("button", button);
            params.put("status", HoldStatus.PRESS.name().toLowerCase());
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
