package com.google.android.apps.authenticator.wear;

import com.google.android.apps.authenticator.common.Tools;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Primosz on 2015-04-02.
 */
public class WearListenerService extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        new ResponseHandler(Tools.PATH_PIN, "", WearListenerService.this).start();
    }
}
