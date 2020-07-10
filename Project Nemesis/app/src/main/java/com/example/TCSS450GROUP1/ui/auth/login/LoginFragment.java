package com.example.TCSS450GROUP1.ui.auth.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.auth0.android.jwt.JWT;
import com.example.TCSS450GROUP1.R;
import com.example.TCSS450GROUP1.databinding.FragmentLoginBinding;
import com.example.TCSS450GROUP1.model.PushyTokenViewModel;
import com.example.TCSS450GROUP1.model.UserInfoViewModel;
import com.example.TCSS450GROUP1.util.PasswordValidator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Joseph Rushford
 * Fragment for logining into the database
 */
public class LoginFragment extends Fragment {
    private PushyTokenViewModel mPushyTokenViewModel;
    private UserInfoViewModel mUserViewModel;
    private FragmentLoginBinding binding;
    private LoginViewModel mLoginModel;
    private PasswordValidator mEmailValidator = PasswordValidator.checkPwdLength(2)
            .and(PasswordValidator.checkExcludeWhiteSpace())
            .and(PasswordValidator.checkPwdSpecialChar("@"));
    private PasswordValidator mPassWordValidator = PasswordValidator.checkPwdLength(7)
            .and(PasswordValidator.checkExcludeWhiteSpace());
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginModel = new ViewModelProvider(getActivity())
                .get(LoginViewModel.class);
        mPushyTokenViewModel = new ViewModelProvider(getActivity())
                .get(PushyTokenViewModel.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //lamba expression example
        binding.buttonToRegister.setOnClickListener(button->processRegister());
        binding.buttonForgot.setOnClickListener(button->processForgotPassword());
        binding.buttonSignIn.setOnClickListener(this::attemptSignIn);
        SharedPreferences preferences = getActivity().getSharedPreferences("theme",Context.MODE_PRIVATE);





        mLoginModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse);
        mPushyTokenViewModel.addTokenObserver(getViewLifecycleOwner(), token ->
                binding.buttonSignIn.setEnabled(!token.isEmpty()));
        mPushyTokenViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observePushyPutResponse);
    }




    private void sendPushyToken() {
        mPushyTokenViewModel.sendTokenToWebservice(mUserViewModel.getJWT());
    }
    private void processForgotPassword() {
        Navigation.findNavController(getView()).navigate(LoginFragmentDirections.actionLoginFragmentToForgotFragment());
    }

    private void processRegister() {
        Navigation.findNavController(getView()).navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment());

    }
    private void attemptSignIn(final View button) {
        validateEmail();
    }
    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editEmail.getText().toString().trim()),
                this::validatePassword,
                result -> binding.editEmail.setError("Please enter a valid Email address."));
    }
    private void validatePassword() {
        mPassWordValidator.processResult(
                mPassWordValidator.apply(binding.editPassword.getText().toString()),
                this::verifyAuthWithServer,
                result -> binding.editPassword.setError("Please enter a valid Password."));
    }
    private void verifyAuthWithServer() {
        mLoginModel.connect(
                binding.editEmail.getText().toString(),
                binding.editPassword.getText().toString()
        );

    }
    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to SignInViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        if(response.length() > 0) {
            if(response.has("code")) {
                try {
                    binding.editEmail.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message")
                    );
                } catch (JSONException e) {
                    Log.e("JSON Error", e.getMessage());
                }
            } else {
                try {
                    mUserViewModel = new ViewModelProvider(getActivity(),
                            new UserInfoViewModel.UserInfoViewModelFactory(
                                    binding.editEmail.getText().toString(),
                                    response.getString("token"), "temp"
                            )).get(UserInfoViewModel.class);
                    sendPushyToken();
//                    navigateToHome(
//                            binding.editEmail.getText().toString(),
//                            response.getString("token")
//                    );
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            }
        } else {
            Log.d("JSON Response", "No Response");
        }

    }
    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to PushyTokenViewModel.
     *
     * @param response the Response from the server
     */
    private void observePushyPutResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                //this error cannot be fixed by the user changing credentials...
                binding.editEmail.setError(
                        "Error Authenticating on Push Token. Please contact support");
            } else {
                navigateToHome(
                        binding.editEmail.getText().toString(),
                        mUserViewModel.getJWT()
                );
            }
        }
    }
    private void navigateToHome(String toString, String token) {
        if (binding.switchSignin.isChecked()) {
            SharedPreferences prefs =
                    getActivity().getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            //Store the credentials in SharedPrefs
            prefs.edit().putString(getString(R.string.keys_prefs_jwt), token).apply();
        }
        Navigation.findNavController(getView()).navigate(

                LoginFragmentDirections.actionLoginFragmentToMainActivity(toString, token));
        getActivity().finish();

    }
    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        if (prefs.contains(getString(R.string.keys_prefs_jwt))) {
            String token = prefs.getString(getString(R.string.keys_prefs_jwt), "");
            JWT jwt = new JWT(token);
            // Check to see if the web token is still valid or not. To make a JWT expire after a
            // longer or shorter time period, change the expiration time when the JWT is
            // created on the web service.
            if (!jwt.isExpired(0)) {
                String email = jwt.getClaim("email").asString();
                navigateToHome(email, token);
                return;
            }
        }
    }




}
