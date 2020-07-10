package com.example.TCSS450GROUP1.ui.connections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.TCSS450GROUP1.databinding.FragmentAddContactBinding;
import com.example.TCSS450GROUP1.model.UserInfoViewModel;

/**
 * @author Sharanjit
 */
public class AddContact extends Fragment {
    private FragmentAddContactBinding binding;
    private AddContactViewModel mAddContactViewModel;
    private UserInfoViewModel mModel;
    public AddContact() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddContactViewModel = new ViewModelProvider(getActivity()).get(AddContactViewModel.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddContactBinding.inflate(inflater);
        ViewModelProvider provider= new ViewModelProvider(getActivity());
        mModel = provider.get(UserInfoViewModel.class);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        binding.addContactButton.setOnClickListener(button->processAdd());


    }

    private void processAdd() {
        mAddContactViewModel.connectAddContact(mModel.getEmail(), binding.newContactEmail.getText().toString(), mModel.getJWT());
        Navigation.findNavController(getView()).navigate(AddContactDirections.actionAddContactToNavigationHome());
    }
}
