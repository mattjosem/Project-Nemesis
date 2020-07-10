package com.example.TCSS450GROUP1.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class UserInfoViewModel extends ViewModel {
    private String mJWT;
    private String mEmail;
    private String mUsername;
    public UserInfoViewModel(String email, String jwt, String username) {
        mEmail = email;
        mJWT = jwt;
        mUsername = username;
    }

    public String getEmail() {
        return mEmail;
    }


    public String getJWT() {
        return mJWT;
    }
    public String getUsername() { return mUsername; }
    public static class UserInfoViewModelFactory implements ViewModelProvider.Factory {
        private final String email;
        private final String jwt;
        private final String username;
        public UserInfoViewModelFactory(String email, String jwt, String username) {
            this.email = email;
            this.jwt = jwt;
            this.username = username;
        }
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if(modelClass == UserInfoViewModel.class) {
                return (T) new UserInfoViewModel(email, jwt, username);

            }
                throw new IllegalArgumentException(
                        "Argument must be: " + UserInfoViewModel.class);
        }
    }



}
