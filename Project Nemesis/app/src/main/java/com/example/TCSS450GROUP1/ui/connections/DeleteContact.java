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

import com.example.TCSS450GROUP1.databinding.FragmentDeleteContactBinding;
import com.example.TCSS450GROUP1.model.UserInfoViewModel;

/**
 * @author Joseph Rushford
 * Fragment to delete a contact(not the best solution but it works)
 */
public class DeleteContact extends Fragment {

    private DeleteContactViewModel mDeleteContractViewModel;
    private Contacts mContact;
    private UserInfoViewModel mModel;
    FragmentDeleteContactBinding binding;
    public DeleteContact() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeleteContractViewModel = new ViewModelProvider(getActivity()).get(DeleteContactViewModel.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        DeleteContactArgs args = DeleteContactArgs.fromBundle(getArguments());
        ViewModelProvider provider= new ViewModelProvider(getActivity());

        mModel = provider.get(UserInfoViewModel.class);
        mContact = args.getContact();
        binding = FragmentDeleteContactBinding.inflate(inflater);

        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.deleteContactButton.setOnClickListener(button-> processDelete());



    }

    /**
     * private helper method to call on database and delete a contact from a user.
     */
    private void processDelete() {

        mDeleteContractViewModel.connectRemoveContact(mModel.getEmail(), mContact.getUserName(), mModel.getJWT());
        Navigation.findNavController(getView()).navigate(DeleteContactDirections.actionDeleteContactToNavigationHome());
    }
}
