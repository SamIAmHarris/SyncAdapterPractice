package com.bignerdranch.android.initialtwittersyncadapter.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bignerdranch.android.initialtwittersyncadapter.account.Authenticator;
import com.bignerdranch.android.initialtwittersyncadapter.contentprovider.DatabaseContract;
import com.google.android.gms.gcm.GcmReceiver;

/**
 * Created by SamMyxer on 4/6/16.
 */
public class SyncBroadcastReceiver extends GcmReceiver {
    private static final String TAG = "SyncBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("Syncomania", "Received GCM notification, request sync");
        Account account = new Account(
                Authenticator.ACCOUNT_NAME, Authenticator.ACCOUNT_TYPE);

        ContentResolver.setIsSyncable(account, DatabaseContract.AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(
                account, DatabaseContract.AUTHORITY, true);
        ContentResolver.requestSync(
                account, DatabaseContract.AUTHORITY, Bundle.EMPTY);
    }
}
