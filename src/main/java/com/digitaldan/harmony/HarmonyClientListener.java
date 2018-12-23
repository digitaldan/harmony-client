package com.digitaldan.harmony;

import com.digitaldan.harmony.config.Activity;
import com.digitaldan.harmony.config.Activity.Status;

public interface HarmonyClientListener {
    public void activityStarted(Activity activity);

    public void activityStatusChanged(Activity activity, Status status);

    public void hubConnected();

    public void hubDisconnected(String cause);
}
