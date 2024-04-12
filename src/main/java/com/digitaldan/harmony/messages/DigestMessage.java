package com.digitaldan.harmony.messages;

import java.util.Map;

import com.digitaldan.harmony.config.Activity.Status;
import com.google.gson.annotations.SerializedName;;

public class DigestMessage extends Message {
    public static final String MIME_TYPE = "connect.stateDigest?notify";
    private static Status[] STATUSES = Status.values();

    @SuppressWarnings("unused")
    private String type;
    @SerializedName("data")
    private Digest digest;

    public DigestMessage() {
    }

    public Digest getDigest() {
        return digest;
    }

    public class Digest {
        private Integer sleepTimerId;
        private Map<String, Object> runningZoneList;
        private Integer contentVersion;
        private String activityId;
        private String errorCode;
        private Integer syncStatus;
        private Map<String, Object> updates;
        private Integer stateVersion;
        private String tzOffset;
        private Integer mode;
        private Integer time;
        private String hubSwVersion;
        private Map<String, Object> deviceSetupState;
        private String tzoffset;
        private Boolean isSetupComplete;
        private Boolean sequence;
        private String discoveryServer;
        private String discoveryServerCF;
        private Boolean activitySetupState;
        private Integer activityStatus;
        private Integer wifiStatus;
        private String tz;
        private String runningActivityList;
        private String IPIRConversionDate;
        private Boolean hubUpdate;
        private Integer configVersion;
        private String accountId;

        public String getActivityId() {
            return activityId;
        }

        public Status getActivityStatus() {
            if (activityStatus < STATUSES.length) {
                return STATUSES[activityStatus];
            }
            return Status.UNKNOWN;
        }

        public Integer getSleepTimerId() {
            return sleepTimerId;
        }

        public Map<String, Object> getRunningZoneList() {
            return runningZoneList;
        }

        public Integer getContentVersion() {
            return contentVersion;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public Integer getSyncStatus() {
            return syncStatus;
        }

        public Map<String, Object> getUpdates() {
            return updates;
        }

        public Integer getStateVersion() {
            return stateVersion;
        }

        public String getTzOffset() {
            return tzOffset;
        }

        public Integer getMode() {
            return mode;
        }

        public Integer getTime() {
            return time;
        }

        public String getHubSwVersion() {
            return hubSwVersion;
        }

        public Map<String, Object> getDeviceSetupState() {
            return deviceSetupState;
        }

        public String getTzoffset() {
            return tzoffset;
        }

        public Boolean getIsSetupComplete() {
            return isSetupComplete;
        }

        public Boolean getSequence() {
            return sequence;
        }

        public String getDiscoveryServer() {
            return discoveryServer;
        }

        public String getDiscoveryServerCF() {
            return discoveryServerCF;
        }

        public Boolean getActivitySetupState() {
            return activitySetupState;
        }

        public Integer getWifiStatus() {
            return wifiStatus;
        }

        public String getTz() {
            return tz;
        }

        public String getRunningActivityList() {
            return runningActivityList;
        }

        public String getIPIRConversionDate() {
            return IPIRConversionDate;
        }

        public Boolean getHubUpdate() {
            return hubUpdate;
        }

        public Integer getConfigVersion() {
            return configVersion;
        }

        public String getAccountId() {
            return accountId;
        }
    }
}
