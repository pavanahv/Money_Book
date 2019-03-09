package com.example.allakumarreddy.moneybook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    private static final int REQUEST_CODE_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    PreferencesCus sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        sp = new PreferencesCus(this);
        if (sp.getData(Utils.getEmail()) != null)
            goToNextActivity(false);
    }

    public void signIn(View view) {
        LoggerCus.d(TAG, "Start sign in");
        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .requestEmail()
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    LoggerCus.d(TAG, "Signed in successfully.");
                    sp.setData(Utils.getEmail(), GoogleSignIn.getLastSignedInAccount(this).getEmail());
                    Toast.makeText(this, "Signed in successfully.", Toast.LENGTH_SHORT).show();
                    goToNextActivity(true);
                } else {
                    LoggerCus.d(TAG, "error in sign in");
                    Toast.makeText(this, "Error in SignIn", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    private void goToNextActivity(boolean restore) {
        if (restore)
            startActivity(new Intent(WelcomeActivity.this, RestoreActivity.class));
        else
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }
}
