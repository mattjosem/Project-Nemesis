package com.example.TCSS450GROUP1.ui.auth.forgotten;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.TCSS450GROUP1.databinding.FragmentForgotBinding;

/**
 * @author Joseph Rushford
 * fragment to send email to change password
 */
public class ForgotFragment extends Fragment {
    private FragmentForgotBinding binding;
    private ForgotViewModel mModel;
    public ForgotFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(getActivity())
                .get(ForgotViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForgotBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        binding.sendEmailButton.setOnClickListener(this::attemptForget);
        binding.backLoginButton.setOnClickListener(this::navigateToLogin);
    }

    private void navigateToLogin(View view) {
        Navigation.findNavController(view).navigate(ForgotFragmentDirections.actionForgotFragmentToLoginFragment2());
    }

    private void attemptForget(View view) {
        mModel.connect(binding.emailRecovery.getText().toString());
    }
}
