package com.example.TCSS450GROUP1.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SettingsViewModel extends ViewModel {
    private String mJWT;
    private String mEmail;
    private String mUsername;
    public SettingsViewModel(String email, String jwt) {
        mEmail = email;
        mJWT = jwt;

    }

    public String getEmail() {
        return mEmail;
    }


    public String getJWT() {
        return mJWT;
    }
    public String getUsername() { return mUsername; }
    public static class SettingViewModelFactory implements ViewModelProvider.Factory {
        private final String email;
        private final String jwt;

        public SettingViewModelFactory(String email, String jwt) {
            this.email = email;
            this.jwt = jwt;

        }
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if(modelClass == SettingsViewModel.class) {
                return (T) new SettingsViewModel(email, jwt);

            }
                throw new IllegalArgumentException(
                        "Argument must be: " + SettingsViewModel.class);
        }
    }



}
