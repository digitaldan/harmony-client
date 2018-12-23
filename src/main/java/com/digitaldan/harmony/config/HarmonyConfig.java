package com.digitaldan.harmony.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class HarmonyConfig {
    @SerializedName("activity")
    private List<Activity> activities = new ArrayList<>();

    @SerializedName("device")
    private List<Device> devices = new ArrayList<>();

    private Map<String, String> content = new HashMap<>();

    private Global global;

    public String toJson() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(this);
    }

    public Map<Integer, String> getDeviceLabels() {
        Map<Integer, String> results = new HashMap<Integer, String>();
        for (Device device : devices) {
            results.put(device.getId(), device.getLabel());
        }
        return results;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activity) {
        this.activities = activity;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> device) {
        this.devices = device;
    }

    public Map<String, String> getContent() {
        return content;
    }

    public void setContent(Map<String, String> content) {
        this.content = content;
    }

    public Global getGlobal() {
        return global;
    }

    public void setGlobal(Global global) {
        this.global = global;
    }

    public Activity getActivityById(int result) {
        for (Activity activity : activities) {
            if (activity.getId() == result) {
                return activity;
            }
        }
        return null;
    }

    public Activity getActivityByName(String label) {
        for (Activity activity : activities) {
            if (activity.getLabel().equals(label)) {
                return activity;
            }
        }
        return null;
    }

    public Device getDeviceByName(String label) {
        for (Device device : devices) {
            if (device.getLabel().equals(label)) {
                return device;
            }
        }
        return null;
    }
}
