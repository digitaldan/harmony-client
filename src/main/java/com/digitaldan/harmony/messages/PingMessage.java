package com.digitaldan.harmony.messages;

import java.util.HashMap;
import java.util.Map;

public class PingMessage {

    public static final String MIME_TYPE = "vnd.logitech.connect/vnd.logitech.ping";

    public static class PingRequestMessage extends RequestMessage {

        HashMap<String, Object> params = new HashMap<>();

        public PingRequestMessage() {
            super(MIME_TYPE);
        }

        @Override
        public Map<String, Object> getParams() {
            return params;
        }

    }

    public static class PingResponseMessage extends ResponseMessage {
        public PingResponseMessage(int code, String id, String msg) {
            super(code, id, msg);
        }
    }
}
