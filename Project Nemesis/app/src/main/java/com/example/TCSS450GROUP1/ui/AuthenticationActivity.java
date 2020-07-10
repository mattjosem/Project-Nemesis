package com.example.TCSS450GROUP1.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.TCSS450GROUP1.R;
import com.example.TCSS450GROUP1.model.PushyTokenViewModel;

import me.pushy.sdk.Pushy;

/**
 * @author  Joseph Rushford
 */
public class AuthenticationActivity extends AppCompatActivity {
    private final int READ_WRITE_PERMISSION_CODE = 1000;
    private SharedPreferences mSharedTheme;
    private static final String THEME_KEY = "currentTheme";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSharedTheme = getSharedPreferences("currentTheme", MODE_PRIVATE);
        mSharedTheme.getString(THEME_KEY, "default");
        if(mSharedTheme.getString(THEME_KEY, "default").equals("default")) {
            getTheme().applyStyle(R.style.AppTheme, true);
        } else {
            getTheme().applyStyle(R.style.OverlayThemePink, true);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        //If it is not already running, start the Pushy listening service
        Pushy.listen(this);
        // Check whether the user has granted us the READ/WRITE_EXTERNAL_STORAGE permissions
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            // Request both READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE so that the
            // Pushy SDK will be able to persist the device token in the external storage
            ActivityCompat.requestPermissions(this,
                    new String[]
                            { Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    READ_WRITE_PERMISSION_CODE);
        } else {
            //User has already granted permission, go retrieve the token.
            initiatePushyTokenRequest();
        }
    }


    private void initiatePushyTokenRequest() {
        new ViewModelProvider(this).get(PushyTokenViewModel.class).retrieveToken();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == READ_WRITE_PERMISSION_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! We may continue...
                // go retrieve the token.
                initiatePushyTokenRequest();
            } else {
                // permission denied, boo!
                // app requires this for Pushy related tasks.
                // end app
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This App requires External Read Write permissions to function")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Close the application, user denied required permissions
                                finishAndRemoveTask();
                            }
                        }).create().show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
