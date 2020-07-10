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

import com.example.TCSS450GROUP1.databinding.FragmentChatListBinding;
import com.example.TCSS450GROUP1.model.UserInfoViewModel;
import com.example.TCSS450GROUP1.ui.MainActivity;


/**
 * @author Jaskaran Mangat
 */
public class ChatListFragment extends Fragment {

    /**
     * Chat List View Model.
     */
    private ChatListViewModel mModel;
    /**
     * Binding for Chat List.
     */
    private FragmentChatListBinding binding;

    /**
     * Empty public constructor.
     */
    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mModel = new ViewModelProvider(getActivity()).get(ChatListViewModel.class);
        if (getActivity() instanceof MainActivity) {
            ViewModelProvider provider= new ViewModelProvider(getActivity());
            UserInfoViewModel model = provider.get(UserInfoViewModel.class);
            mModel.setUserInfoViewModel(model);
        }
        mModel.connectGet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChatListBinding.inflate(inflater);
        binding.buttonAddChatlist.setOnClickListener(view->Navigation.findNavController(view).navigate(ChatListFragmentDirections.actionNavigationChatToAddChatFragment()));
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentChatListBinding binding = FragmentChatListBinding.bind(getView());

        mModel.addChatListObserver(getViewLifecycleOwner(), chatRoomList -> {

            if (!chatRoomList.isEmpty()) {

                binding.chatlistRoot.setAdapter(new ChatListRecyclerViewAdapter(chatRoomList, this));
            }

        });
    }
    public void deleteChat(int chatId) {
        mModel.connectDeleteChat(chatId);
    }
}
