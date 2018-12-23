package com.digitaldan.harmony.messages;

;

public class EventMessage extends Message {
    // /**
    // * harmony.engine?startActivityFinished : "data":{"activityId":"-1","errorCode":"200","errorString":"OK"}}
    // *
    // {"type":"connect.stateDigest?notify","data":{"sleepTimerId":-1,"runningZoneList":[],"contentVersion":104,"activityId":"-1","errorCode":"200","syncStatus":0,"updates":[],"stateVersion":185,"tzOffset":"-28800","mode":3,"time":1545537059,"hubSwVersion":"4.15.206","deviceSetupState":[],"tzoffset":"-28800","isSetupComplete":true,"sequence":false,"discoveryServer":"http:\/\/svcs.myharmony.com\/Discovery\/Discovery.svc","discoveryServerCF":"https:\/\/cf-svcs.myharmony.com\/Discovery\/Discovery.svc","activitySetupState":false,"activityStatus":0,"wifiStatus":1,"tz":"PST8PDT,M3.2.0,M11.1.0","runningActivityList":"","IPIRConversionDate":"","hubUpdate":false,"configVersion":109,"accountId":"000000000"}}
    // *
    // {"type":"connect.stateDigest?notify","data":{"sleepTimerId":-1,"runningZoneList":[],"contentVersion":104,"activityId":"31996621","errorCode":"200","syncStatus":0,"updates":[],"stateVersion":182,"tzOffset":"-28800","mode":3,"time":1545537059,"hubSwVersion":"4.15.206","deviceSetupState":[],"tzoffset":"-28800","isSetupComplete":true,"sequence":false,"discoveryServer":"http:\/\/svcs.myharmony.com\/Discovery\/Discovery.svc","discoveryServerCF":"https:\/\/cf-svcs.myharmony.com\/Discovery\/Discovery.svc","activitySetupState":false,"activityStatus":2,"wifiStatus":1,"tz":"PST8PDT,M3.2.0,M11.1.0","runningActivityList":"31996621","IPIRConversionDate":"","hubUpdate":false,"configVersion":109,"accountId":"000000000"}}
    // *
    // *
    // {"type":"connect.stateDigest?notify","data":{"sleepTimerId":-1,"runningZoneList":[],"contentVersion":104,"activityId":"31996619","errorCode":"200","syncStatus":0,"updates":[],"stateVersion":189,"tzOffset":"-28800","mode":3,"time":1545537285,"hubSwVersion":"4.15.206","deviceSetupState":[],"tzoffset":"-28800","isSetupComplete":true,"sequence":false,"discoveryServer":"http:\/\/svcs.myharmony.com\/Discovery\/Discovery.svc","discoveryServerCF":"https:\/\/cf-svcs.myharmony.com\/Discovery\/Discovery.svc","activitySetupState":false,"activityStatus":2,"wifiStatus":1,"tz":"PST8PDT,M3.2.0,M11.1.0","runningActivityList":"","IPIRConversionDate":"","hubUpdate":false,"configVersion":109,"accountId":"10950646"}}
    // *
    // {"type":"harmony.engine?startActivityFinished","data":{"activityId":"31996619","errorCode":"200","errorString":"OK"}}
    // *
    // {"type":"connect.stateDigest?notify","data":{"sleepTimerId":-1,"runningZoneList":[],"contentVersion":104,"activityId":"31996619","errorCode":"200","syncStatus":0,"updates":[],"stateVersion":190,"tzOffset":"-28800","mode":3,"time":1545537285,"hubSwVersion":"4.15.206","deviceSetupState":[],"tzoffset":"-28800","isSetupComplete":true,"sequence":false,"discoveryServer":"http:\/\/svcs.myharmony.com\/Discovery\/Discovery.svc","discoveryServerCF":"https:\/\/cf-svcs.myharmony.com\/Discovery\/Discovery.svc","activitySetupState":false,"activityStatus":2,"wifiStatus":1,"tz":"PST8PDT,M3.2.0,M11.1.0","runningActivityList":"31996619","IPIRConversionDate":"","hubUpdate":false,"configVersion":109,"accountId":"10950646"}}
    // *
    // *
    // */
    //
    // public static final String TYPE_FINISHED = "harmony.engine?startActivityFinished";
    // public static final String TYPE_DIGEST = "connect.stateDigest?notify";
    // private static Status[] STATUSES = Status.values();
    //
    // private String type;
    // private String activityId;
    // private int activityStatus;
    // private String errorCode;
    //
    // public EventMessage() {
    // }
    //
    // public enum EventType {
    // STATE_DIGEST,
    // START_ACTIVITY_FINISHED,
    // }
    //
    // public EventType getType() {
    // if (TYPE_FINISHED.equals(type)) {
    // return EventType.START_ACTIVITY_FINISHED;
    // } else {
    // return EventType.STATE_DIGEST;
    // }
    // }
    //
    // private void setType(String type) {
    // this.type = type;
    // }
    //
    // public Integer getActivityId() {
    // if (activityId != null) {
    // try {
    // return Integer.parseInt(activityId);
    // } catch (NumberFormatException ignored) {
    // }
    // }
    // return null;
    // }
    //
    // public Status getActivityStatus() {
    //
    // if (activityStatus < STATUSES.length) {
    // return STATUSES[activityStatus];
    // }
    // return Status.UNKNOWN;
    // }
    //
    // public String getErrorCode() {
    // return errorCode;
    // }
    //
    // static EventMessage FromJSON(JsonObject jsonObject) {
    // Gson gson = new Gson();
    // EventMessage em = gson.fromJson(jsonObject, EventMessage.class);
    // // small hack to get around more complicated serialization
    // em.setType(jsonObject.get("type").getAsString());
    // return em;
    // }
}
