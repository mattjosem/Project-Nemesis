package com.example.TCSS450GROUP1.ui.auth.register;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.TCSS450GROUP1.R;
import com.example.TCSS450GROUP1.databinding.FragmentRegisterBinding;
import com.example.TCSS450GROUP1.databinding.FragmentVerifyBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyFragment extends Fragment {

    public VerifyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        FragmentVerifyBinding binding = FragmentVerifyBinding.bind(getView());


        binding.backLoginButton.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(
                        VerifyFragmentDirections
                                .actionVerifyFragmentToLoginFragment()));
    }
}
