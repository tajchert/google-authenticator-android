package com.google.android.apps.authenticator.wear;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

public class ResponseHandler extends Thread {
    private String text;
    private Context context;
    private String path;
    private static final String TAG = "TestTag";


    public ResponseHandler(String messagePath, String pinCode, Context ctx) {
        context = ctx;
        path = messagePath;
        this.text = pinCode;
    }



    public void run() {
        if ((text.getBytes().length / 1024) > 100) {
            throw new RuntimeException("Object is too big to push it via Google Play Services");
        }

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the Data Layer API
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.blockingConnect(200, TimeUnit.MILLISECONDS);//TODO timeout extract
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            MessageApi.SendMessageResult result;
            result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, text.getBytes()).await();
            if (!result.getStatus().isSuccess()) {
                Log.v(TAG, "ERROR: failed to send Message via Google Play Services");
            }
        }
    }
}