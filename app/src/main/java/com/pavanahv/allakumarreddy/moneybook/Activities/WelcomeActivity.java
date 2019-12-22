package com.pavanahv.allakumarreddy.moneybook.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.pavanahv.allakumarreddy.moneybook.R;
import com.pavanahv.allakumarreddy.moneybook.storage.PreferencesCus;
import com.pavanahv.allakumarreddy.moneybook.utils.LoggerCus;
import com.pavanahv.allakumarreddy.moneybook.utils.Utils;

import java.util.Collections;

public class WelcomeActivity extends BaseActivity {

    private static final String TAG = "WelcomeActivity";
    private static final int REQUEST_CODE_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void signIn(View view) {
        LoggerCus.d(TAG, "Start sign in");
        requestSignIn();
    }

    private void requestSignIn() {
        LoggerCus.d(TAG, "Requesting sign-in");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                } else {
                    Toast.makeText(this, "Something went wrong not able to Sign In. Please try again later", Toast.LENGTH_LONG).show();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }


    private void handleSignInResult(Intent result) {
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);
        // Use the authenticated account to sign in to the Drive service.
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        this, Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(googleAccount.getAccount());
        Drive googleDriveService =
                new Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(),
                        credential)
                        .setApplicationName("MoneyBook")
                        .build();
        PreferencesCus sp = new PreferencesCus(this);
        sp.setData(Utils.getEmail(), GoogleSignIn.getLastSignedInAccount(this).getEmail());
        startActivity(new Intent(this, RestoreActivity.class));
        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
        finish();
    }

    private void goToNextActivity(boolean restore) {
        if (restore)
            startActivity(new Intent(WelcomeActivity.this, RestoreActivity.class));
        else
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
        finish();
    }
}
