package com.bignerdranch.android.initialtwittersyncadapter.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by SamMyxer on 4/6/16.
 */
public class SyncService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new SyncAdapter(this, true).getSyncAdapterBinder();
    }
}
