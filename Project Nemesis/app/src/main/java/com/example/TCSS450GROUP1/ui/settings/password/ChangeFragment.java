package com.example.TCSS450GROUP1.ui.settings.password;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.TCSS450GROUP1.R;
import com.example.TCSS450GROUP1.databinding.FragmentChangeBinding;
import com.example.TCSS450GROUP1.databinding.FragmentDeleteBinding;
import com.example.TCSS450GROUP1.model.SettingsViewModel;
import com.example.TCSS450GROUP1.model.UserInfoViewModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeFragment extends Fragment {
    private FragmentChangeBinding binding;
    private ChangeViewModel mChangeModel;
    private SettingsViewModel mSettingsModel;
    public ChangeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChangeModel = new ViewModelProvider(getActivity())
                .get(ChangeViewModel.class);
        mSettingsModel = new ViewModelProvider((getActivity())).get(SettingsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangeBinding.inflate(inflater);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonUpdate.setOnClickListener(this::attemptUpdate);
//        Log.d("checking", mUserModel.getEmail());
        mChangeModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }

    private void attemptUpdate(View view) {
        mChangeModel.connect(
                mSettingsModel.getEmail(),
                binding.editPassword1.getText().toString(),
                binding.editPassword2.getText().toString(),
                mSettingsModel.getJWT()
        );
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to ChangeViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.editPassword1.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {

            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
//        super.onCreate((savedInstanceState));
//    }
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.drop_down, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if(id == R.id.action_change_theme) {
//
//
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
