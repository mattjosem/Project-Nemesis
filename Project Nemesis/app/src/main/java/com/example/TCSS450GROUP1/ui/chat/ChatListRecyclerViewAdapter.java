package com.example.TCSS450GROUP1.ui.chat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TCSS450GROUP1.R;
import com.example.TCSS450GROUP1.databinding.FragmentChatlistCardBinding;

import org.json.JSONException;

import java.util.List;

/**
 * @author Jaskaran Mangat
 */
public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.ChatListViewHolder> {


    /**
     * List of Chat rooms.
     */
    List<ChatRoom> mChatRooms;

    private final ChatListFragment mParent;

    /**
     * Constructor that builds the recycler view adapter from
     * a list of Chat rooms.
     *
     * @param chats List of chat rooms.
     */
    public ChatListRecyclerViewAdapter(List<ChatRoom> chats, ChatListFragment parent) {
        this.mChatRooms = chats;
        this.mParent = parent;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ChatListViewHolder(LayoutInflater
                .from(parent.getContext()).inflate(R.layout.fragment_chatlist_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {

        try {
            holder.setChatRoom(mChatRooms.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        return mChatRooms.size();
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        public FragmentChatlistCardBinding binding;


        public ChatListViewHolder(View view) {
            super(view);
            mView = view;
//
            binding = FragmentChatlistCardBinding.bind(view);

        }

        void setChatRoom(final ChatRoom chatRoom) throws JSONException {
            binding.deleteChatButton.setOnClickListener(view -> deleteChat(this, chatRoom));
            binding.navigateToChatroom.setOnClickListener(view -> Navigation.findNavController(mView).
                    navigate(ChatListFragmentDirections.actionChatListFragmentToNavigationChat(chatRoom.getChatId())));
            binding.addToChatButton.setOnClickListener((view -> Navigation.findNavController(mView).
                    navigate(ChatListFragmentDirections.actionNavigationChatToAddToChatFragment(chatRoom.getChatId()))));
            for (int i = 0; i < chatRoom.getRowCount(); i++) {
                binding.chatRoomTextView.setText("" + chatRoom.getEmail());
            }
        }


        private void deleteChat(final ChatListViewHolder view, final ChatRoom chatRoom) {
            final int chatId = chatRoom.getChatId();
            mParent.deleteChat(chatId);
            Log.d("ChatListRecycle", "Removed chatroom with ID: " + chatId);
            Navigation.findNavController(mView).navigate(ChatListFragmentDirections.actionNavigationChatToNavigationHome());
        }
    }
}
