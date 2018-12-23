package com.digitaldan.harmony.config;

import com.google.gson.annotations.SerializedName;

public class Discovery {
    private String setupSessionClient;
    private String discoveryServerUriCF;
    private String setupSessionSetupType;
    @SerializedName("current_fw_version")
    private String currentFwVersion;
    private String ip;
    private Integer port;
    private String productId;
    private Integer setupStatus;
    private Integer mode;
    private String uuid;
    private String oohEnabled;
    private String friendlyName;
    private String remoteId;
    private String discoveryServerUri;
    private String openApiVersion;
    private String email;
    private String minimumOpenApiClientVersionRequired;
    private String recommendedOpenApiClientVersion;
    private String protocolVersion;
    private String host_name;
    private Integer hubId;
    private Integer setupSessionType;
    private Boolean setupSessionIsStale;
    private String accountId;

    public String getSetupSessionClient() {
        return setupSessionClient;
    }

    public String getDiscoveryServerUriCF() {
        return discoveryServerUriCF;
    }

    public String getSetupSessionSetupType() {
        return setupSessionSetupType;
    }

    public String getCurrentFwVersion() {
        return currentFwVersion;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    public String getProductId() {
        return productId;
    }

    public Integer getSetupStatus() {
        return setupStatus;
    }

    public Integer getMode() {
        return mode;
    }

    public String getUuid() {
        return uuid;
    }

    public String getOohEnabled() {
        return oohEnabled;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public String getDiscoveryServerUri() {
        return discoveryServerUri;
    }

    public String getOpenApiVersion() {
        return openApiVersion;
    }

    public String getEmail() {
        return email;
    }

    public String getMinimumOpenApiClientVersionRequired() {
        return minimumOpenApiClientVersionRequired;
    }

    public String getRecommendedOpenApiClientVersion() {
        return recommendedOpenApiClientVersion;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getHost_name() {
        return host_name;
    }

    public Integer getHubId() {
        return hubId;
    }

    public Integer getSetupSessionType() {
        return setupSessionType;
    }

    public Boolean getSetupSessionIsStale() {
        return setupSessionIsStale;
    }

    public String getAccountId() {
        return accountId;
    }

}
