package com.example.TCSS450GROUP1.ui.connections.sharanjit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.TCSS450GROUP1.R;
import com.example.TCSS450GROUP1.model.UserInfoViewModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */

/**
 * @author sharanjitsingh
 */
public class ConnectionFragment extends Fragment {


    private UserInfoViewModel mUserModel;
    private ArrayList<String> mContacts;
    public ConnectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connection, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.textPubdate.setText(args.getBlog().getPubDate());
//        binding.textTitle.setText(args.getBlog().getTitle());
//
//        final String preview =  Html.fromHtml(
//                args.getBlog().getTeaser(),
//                Html.FROM_HTML_MODE_COMPACT).toString();
//
//        binding.textPreview.setText(preview);
//
//
//        //Note we are using an Intent here to start the default system web browser
//        binding.buttonUrl.setOnClickListener(button ->
//                startActivity(new Intent(Intent.ACTION_VIEW,
//                        Uri.parse(args.getBlog().getUrl()))));
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContacts = new ArrayList<>();
        ViewModelProvider provider= new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);

    }
}
