package com.digitaldan.harmony.messages;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class GetCurrentActivityMessage {

    public static final String MIME_TYPE = "vnd.logitech.harmony/vnd.logitech.harmony.engine?getCurrentActivity";

    public static class GetCurrentActivityRequestMessage extends RequestMessage {

        HashMap<String, Object> params = new HashMap<>();

        public GetCurrentActivityRequestMessage() {
            super(MIME_TYPE);
        }

        @Override
        public Map<String, Object> getParams() {
            return params;
        }

    }

    public static class GetCurrentActivityResponseMessage extends ResponseMessage {

        @SerializedName("data")
        private CurrentActivityResult currentActivityResult;

        public GetCurrentActivityResponseMessage(int code, String id, String msg) {
            super(code, id, msg);
        }

        public int getActivityId() {
            try {
                return Integer.parseInt(currentActivityResult.result);
            } catch (Exception e) {
                return -1;
            }
        }

        private class CurrentActivityResult {
            private String result;

        }
    }
}
