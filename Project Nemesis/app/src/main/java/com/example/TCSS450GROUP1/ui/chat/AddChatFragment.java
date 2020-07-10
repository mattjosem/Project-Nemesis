package com.example.TCSS450GROUP1.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.TCSS450GROUP1.databinding.FragmentAddChatBinding;
import com.example.TCSS450GROUP1.model.UserInfoViewModel;

/**
 * @author Nick Caron
 * A simple {@link Fragment} subclass.
 */
public class AddChatFragment extends Fragment {
    private FragmentAddChatBinding binding;
    private AddChatViewModel mAddContactViewModel;
    private UserInfoViewModel mModel;
    public AddChatFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddContactViewModel = new ViewModelProvider(getActivity()).get(AddChatViewModel.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddChatBinding.inflate(inflater);
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
        if(binding.newContactEmail.getText().toString() != "" && binding.newChatName.getText().toString() != ""){
            mAddContactViewModel.connectAddChat(mModel.getJWT(),binding.newContactEmail.getText().toString(),binding.newChatName.getText().toString());
            Navigation.findNavController(getView()).navigate(AddChatFragmentDirections.actionAddChatFragmentToNavigationHome());
        }
    }
}
