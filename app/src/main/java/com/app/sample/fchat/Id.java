package com.app.sample.fchat;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * Created by jasmeetkaur on 24/08/18.
 */

public class Id {
    String uid = "jkj";
    public void setId(String id)
    {
        uid = id;
        System.out.println("EMAIL ID OF USER IS : "+uid);
    }
    public String getId()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        System.out.println("EMAIL ID OF USER IS GET : ");
        return uid;
    }
}
