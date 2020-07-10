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

import com.example.TCSS450GROUP1.databinding.FragmentAddToChatBinding;
import com.example.TCSS450GROUP1.model.UserInfoViewModel;

/**
 * @author Joseph Rushford
 */
public class AddToChatFragment extends Fragment {
    private FragmentAddToChatBinding binding;
    private AddToChatViewModel mAddToChatViewModel;
    private UserInfoViewModel mModel;
    private int mChatID;
    public AddToChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddToChatViewModel = new ViewModelProvider(getActivity()).get(AddToChatViewModel.class);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        AddToChatFragmentArgs args = AddToChatFragmentArgs.fromBundle(getArguments());
        mChatID = args.getChatID();
        binding = FragmentAddToChatBinding.inflate(inflater);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mModel = provider.get(UserInfoViewModel.class);
        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        binding.addToChatButton.setOnClickListener(button->processAdd());


    }

    private void processAdd() {
        mAddToChatViewModel.connectAddToChat(mModel.getJWT(), binding.newEmailText.getText().toString(), String.valueOf(mChatID));
        Navigation.findNavController(getView()).navigate(AddToChatFragmentDirections.actionAddToChatFragmentToNavigationHome());
    }
}
