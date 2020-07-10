package com.example.TCSS450GROUP1.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.TCSS450GROUP1.R;
import com.example.TCSS450GROUP1.model.SettingsViewModel;

/**
 * Activity for the Settings in the drop down menu
 * @author Joseph Rushford
 */
public class AccountActivity extends AppCompatActivity {
    private SharedPreferences mSharedTheme;
    private String mJWT;
    private String mEmail;
    private static final String THEME_KEY = "currentTheme";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSharedTheme = getSharedPreferences("currentTheme", MODE_PRIVATE);
        if(getIntent().getExtras() != null) {
            mJWT = getIntent().getExtras().getString("jwt");
            mEmail = getIntent().getExtras().getString("email");
            System.out.println(mEmail);

        }
        new ViewModelProvider(this, new SettingsViewModel.SettingViewModelFactory(mEmail, mJWT)).get(SettingsViewModel.class);
        mSharedTheme.getString(THEME_KEY, "default");
        if(mSharedTheme.getString(THEME_KEY, "default").equals("default")) {
            getTheme().applyStyle(R.style.AppTheme, true);
        } else {
            getTheme().applyStyle(R.style.OverlayThemePink, true);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
    }


}
