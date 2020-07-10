package com.example.TCSS450GROUP1.ui.connections;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TCSS450GROUP1.R;
import com.example.TCSS450GROUP1.databinding.FragmentContactlistCardBinding;

import java.util.List;

/**
 * Recycler View for Contacts
 * @author Joseph Rushford
 */
public class ContactListRecyclerViewAdapter extends RecyclerView.Adapter<ContactListRecyclerViewAdapter.ContactListViewHolder> {
    List<Contacts> mContacts;

    public ContactListRecyclerViewAdapter(List<Contacts> contacts) {
        this.mContacts=contacts;
    }
    @NonNull
    @Override
    public ContactListViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        return new ContactListViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.fragment_contactlist_card, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull ContactListViewHolder holder, int position) {
            holder.setContact(mContacts.get(position));



    }

    /**
     * helper method to check the size of the contact list
     * @author Joseph Rushford
     * @return the size of the contact list
     */
    @Override
    public int getItemCount() {
        return mContacts.size();

    }

    /**
     * Inner class to create the card view for each contact
     * @author Joseph Rushford
     */
    public class ContactListViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentContactlistCardBinding binding;
        public ContactListViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentContactlistCardBinding.bind(view);


        }
        void setContact(final Contacts contact) {
           binding.deleteContactButton.setOnClickListener(button->progressDeletion(contact));
            binding.contactlistTextView.setText(""+ contact.getUserName());
            binding.createChatButton.setOnClickListener(button->progressChat(contact));

        }

        private void progressDeletion(Contacts contact) {
            Navigation.findNavController(mView).
                    navigate(ContactsListFragmentDirections.actionNavigationConnectionsToDeleteContact(contact));
           // mContacts.remove(contact);
          //  notifyDataSetChanged();


        }
        private void progressChat(Contacts contact) {
            Navigation.findNavController(mView)
                    .navigate(ContactsListFragmentDirections.actionNavigationConnectionsToContactChatFragment(contact));
        }
    }
}
