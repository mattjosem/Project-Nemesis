package com.example.TCSS450GROUP1.ui.connections;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.TCSS450GROUP1.R;
import com.example.TCSS450GROUP1.databinding.FragmentContactChatBinding;
import com.example.TCSS450GROUP1.model.UserInfoViewModel;
import com.example.TCSS450GROUP1.ui.chat.AddChatFragmentDirections;
import com.example.TCSS450GROUP1.ui.chat.AddChatViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactChatFragment extends Fragment {
    private AddChatViewModel mAddChatViewModel;
    private FragmentContactChatBinding binding;
    private UserInfoViewModel mModel;
    private Contacts mContact;
    public ContactChatFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddChatViewModel = new ViewModelProvider(getActivity()).get(AddChatViewModel.class);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContactChatBinding.inflate(inflater);
        ViewModelProvider provider= new ViewModelProvider(getActivity());
        mModel = provider.get(UserInfoViewModel.class);
        ContactChatFragmentArgs args = ContactChatFragmentArgs.fromBundle(getArguments());
        mContact = args.getContact();
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        binding.chatContactCreateButton.setOnClickListener(button->processAdd());


    }

    private void processAdd() {
        if(binding.chatContactCreateText.getText().toString() != ""){
            mAddChatViewModel.connectAddChat(mModel.getJWT(),mContact.getUserName(),binding.chatContactCreateText.getText().toString());
            Navigation.findNavController(getView()).navigate(ContactChatFragmentDirections.actionContactChatFragmentToNavigationHome());
        }
    }


}
