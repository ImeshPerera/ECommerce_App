package com.imeshperera.ecomapp.utils;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.imeshperera.ecomapp.R;

public class GoogleSignInHelper {
    public static GoogleSignInClient getClient(Context context) {
        // TODO: After enabling Google Sign-In in Firebase Console and re-downloading
        // google-services.json, replace requestEmail() below with:
        //   .requestIdToken(context.getString(R.string.default_web_client_id))
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .build();
        return GoogleSignIn.getClient(context, gso);
    }
}
