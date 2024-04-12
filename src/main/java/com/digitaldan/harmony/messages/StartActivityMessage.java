package com.digitaldan.harmony.messages;

import java.util.HashMap;
import java.util.Map;

public class StartActivityMessage {
    public static final String MIME_TYPE = "harmony.activityengine?runactivity";

    public static class StartActivityRequestMessage extends RequestMessage {

        HashMap<String, Object> params = new HashMap<>();

        public StartActivityRequestMessage(String activityId, long timeStamp) {
            super(MIME_TYPE);
            HashMap<String, Object> args = new HashMap<>();
            args.put("rule", "start");
            params.put("activityId", activityId);
            params.put("timeStamp", timeStamp);
            params.put("args", args);
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
