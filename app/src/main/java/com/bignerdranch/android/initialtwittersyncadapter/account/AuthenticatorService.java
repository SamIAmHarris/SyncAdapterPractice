package com.bignerdranch.android.initialtwittersyncadapter.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by SamMyxer on 4/6/16.
 */
public class AuthenticatorService extends Service {

    private Authenticator authenticator;

    public AuthenticatorService() {
        this.authenticator = new Authenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
