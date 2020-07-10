package com.example.TCSS450GROUP1.ui.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.TCSS450GROUP1.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //lamba expression example
        binding.changePwButton.setOnClickListener(button->processChange());
        binding.deleteAccountButton.setOnClickListener(button->processDelete());
    }


    /**
     * @author Joseph Rushford
     * process view to deletefragment where user can delete account
     */
    private void processDelete() {
        Navigation.findNavController(getView()).
                navigate(SettingsFragmentDirections.actionSettingsFragmentToDeleteFragment());
    }

    /**
     * @author Joseph Rushford
     * process view to ChangeFragment to change password
     */
    private void processChange() {
        Navigation.findNavController(getView()).
                navigate(SettingsFragmentDirections.actionSettingsFragmentToChangeFragment());
    }
}
