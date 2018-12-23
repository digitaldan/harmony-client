package com.digitaldan.harmony.config;

import com.google.gson.annotations.Expose;

public class Global {
    @Expose
    private String timeStampHash;
    @Expose
    private String locale;

    public String getTimeStampHash() {
        return timeStampHash;
    }

    public void setTimeStampHash(String timeStampHash) {
        this.timeStampHash = timeStampHash;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
