package com.example.TCSS450GROUP1.ui.auth.register;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.TCSS450GROUP1.R;
import com.example.TCSS450GROUP1.databinding.FragmentLoginBinding;
import com.example.TCSS450GROUP1.databinding.FragmentRegisterBinding;
import com.example.TCSS450GROUP1.ui.auth.login.LoginFragmentDirections;
import com.example.TCSS450GROUP1.util.PasswordValidator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private RegisterViewModel mRegisterModel;
    private PasswordValidator mEmailValidator = PasswordValidator.checkPwdLength(2)
            .and(PasswordValidator.checkExcludeWhiteSpace())
            .and(PasswordValidator.checkPwdSpecialChar("@"));

    private PasswordValidator mPassWordValidator =
            PasswordValidator.checkPwdLength(7)
                    .and(PasswordValidator.checkExcludeWhiteSpace());

    private PasswordValidator mPassWordMatchValidator =
            PasswordValidator.checkClientPredicate(pwd -> pwd.equals(binding.editPassword2.getText().toString()));
    private PasswordValidator mUsernameValidator =
            PasswordValidator.checkPwdLength(3);
    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegisterModel = new ViewModelProvider(getActivity()).get(RegisterViewModel.class);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        binding.buttonRegister.setOnClickListener(this::attemptRegister);
        mRegisterModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }
    private void attemptRegister(final View button) { validateEmail(); }
    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editEmail.getText().toString().trim()),
                this::validateUsername,
                result -> binding.editEmail.setError("Please enter a valid Email address."));
    }
    private void validateUsername() {
        mUsernameValidator.processResult(
                mUsernameValidator.apply(binding.editUsername.getText().toString().trim()),
                this::validatePassword,
                result -> binding.editUsername.setError("Please enter a valid user name of length 3 or more."));
    }
    private void validatePassword() {
        mPassWordValidator.processResult(
                mPassWordValidator.apply(binding.editPassword1.getText().toString()),
                this::validateMatchPassword,
                result -> binding.editPassword1.setError("Please enter a valid Password."));
    }

    private void validateMatchPassword() {
        mPassWordMatchValidator.processResult(
                mPassWordValidator.apply(binding.editPassword2.getText().toString()),
                this::verifyAuthWithServer,
                result -> binding.editPassword2.setError("Passwords do not match"));
    }

    private void verifyAuthWithServer() {
        mRegisterModel.connect(
                binding.editFirst.getText().toString(),
                binding.editLast.getText().toString(),
                binding.editUsername.getText().toString(),
                binding.editEmail.getText().toString(),
                binding.editPassword1.getText().toString()

        );
    }
    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to LoginViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        if(response.length() > 0) {
            if(response.has("code")) {
                try {
                    binding.editEmail.setError("Error Authenticating: " +
                            response.getJSONObject("data").getString("message"));

                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {


                navigateToVerify(binding.editEmail.getText().toString(),
                        "");

            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
    private void navigateToVerify(final String email, final String password) {

                Navigation.findNavController(getView()).navigate(
                        RegisterFragmentDirections
                                .actionRegisterFragmentToVerifyFragment(binding.editEmail.getText().toString(),
                                        binding.editPassword1.getText().toString()));
    }



}
