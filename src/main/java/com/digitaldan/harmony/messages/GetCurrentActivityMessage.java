package com.digitaldan.harmony.messages;

import java.util.HashMap;
import java.util.Map;

import com.digitaldan.harmony.config.Activity;
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
        private Activity activity;

        public GetCurrentActivityResponseMessage(int code, String id, String msg, Activity activity) {
            super(code, id, msg);
            this.activity = activity;
        }

        public Activity getActivity() {
            return activity;
        }
    }
}
