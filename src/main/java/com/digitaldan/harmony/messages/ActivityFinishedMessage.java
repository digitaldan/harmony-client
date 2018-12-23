package com.digitaldan.harmony.messages;

import com.google.gson.annotations.SerializedName;

public class ActivityFinishedMessage extends Message {
    public static final String MIME_TYPE = "harmony.engine?startActivityFinished";
    @SuppressWarnings("unused")
    private String type;
    @SerializedName("data")
    private ActivityFinished activityFinished;

    public ActivityFinishedMessage() {
    }

    public ActivityFinished getActivityFinished() {
        return activityFinished;
    }

    public static class ActivityFinished {
        Integer activityId;
        Integer errorCode;
        String errorString;

        public ActivityFinished() {

        }

        public Integer getActivityId() {
            return activityId;
        }

        public Integer getErrorCode() {
            return errorCode;
        }

        public String getErrorString() {
            return errorString;
        }

    }
}
