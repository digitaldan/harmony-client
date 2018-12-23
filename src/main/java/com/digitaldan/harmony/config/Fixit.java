package com.digitaldan.harmony.config;

import com.google.gson.annotations.SerializedName;

public class Fixit {
    private String id;

    @SerializedName("Power")
    private PowerState power;

    @SerializedName("Input")
    private String input;

    @SerializedName("isAlwaysOn")
    private boolean alwaysOn;

    @SerializedName("isRelativePower")
    private boolean relativePower;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PowerState getPower() {
        return power;
    }

    public void setPower(PowerState power) {
        this.power = power;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public boolean isAlwaysOn() {
        return alwaysOn;
    }

    public void setAlwaysOn(boolean alwaysOn) {
        this.alwaysOn = alwaysOn;
    }

    public boolean isRelativePower() {
        return relativePower;
    }

    public void setRelativePower(boolean relativePower) {
        this.relativePower = relativePower;
    }
}
