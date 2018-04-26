package com.ashwingk.treasurehunt;

import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;

/**
 * Created by Ashman on 02-10-2017.
 */

public class FirebaseIdService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIdService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }
    public void sendRegistrationToServer(String refreshedToken) {
        String uid = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.INSTANCE.getUID_PREFERENCE(), "");
        if(!uid.equals("")) {
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("token", refreshedToken);
            FirebaseDatabase.getInstance().getReference("users/"+uid).updateChildren(hm);
        }
    }
}
