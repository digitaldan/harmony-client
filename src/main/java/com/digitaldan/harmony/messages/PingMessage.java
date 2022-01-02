package com.digitaldan.harmony.messages;

import java.util.HashMap;
import java.util.Map;

import com.digitaldan.harmony.config.Ping;
import com.google.gson.annotations.SerializedName;

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

        @SerializedName("data")
        private Ping ping;

        public PingResponseMessage(int code, String id, String msg, Ping ping) {
            super(code, id, msg);
            this.ping = ping;
        }

        public Ping getPing() {
            return ping;
        }
    }
}
