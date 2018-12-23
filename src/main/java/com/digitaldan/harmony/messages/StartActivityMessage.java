package com.digitaldan.harmony.messages;

import java.util.HashMap;
import java.util.Map;

public class StartActivityMessage {
    public static final String MIME_TYPE = "vnd.logitech.harmony/vnd.logitech.harmony.engine?startactivity";

    public static class StartActivityRequestMessage extends RequestMessage {

        HashMap<String, Object> params = new HashMap<>();

        public StartActivityRequestMessage(int activityId, long timeStamp) {
            super(MIME_TYPE);
            params.put("activityId", activityId);
            params.put("timeStamp", timeStamp);
        }

        @Override
        public Map<String, Object> getParams() {
            return params;
        }

    }

    public static class StartActivityResponseMethod extends ResponseMessage {

        public StartActivityResponseMethod(int code, String id, String msg) {
            super(code, id, msg);
        }
    }
}
