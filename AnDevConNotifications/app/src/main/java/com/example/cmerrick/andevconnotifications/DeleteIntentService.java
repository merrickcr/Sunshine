package com.example.cmerrick.andevconnotifications;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by atv684 on 7/29/15.
 */
public class DeleteIntentService extends IntentService {

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent != null){
            Log.e("asdf", "Notification Deleted");
        }
    }

    public DeleteIntentService(){
        super("DeleteServiceIntent");
    }
}
