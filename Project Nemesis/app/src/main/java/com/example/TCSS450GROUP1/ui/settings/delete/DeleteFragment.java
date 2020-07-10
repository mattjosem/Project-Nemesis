package com.example.TCSS450GROUP1.ui.settings.delete;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.TCSS450GROUP1.R;
import com.example.TCSS450GROUP1.databinding.FragmentDeleteBinding;
import com.example.TCSS450GROUP1.model.PushyTokenViewModel;
import com.example.TCSS450GROUP1.model.SettingsViewModel;
import com.example.TCSS450GROUP1.model.UserInfoViewModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeleteFragment extends Fragment {
    private FragmentDeleteBinding binding;
    private DeleteViewModel mDeleteModel;
    private SettingsViewModel mSettingsModel;

    public DeleteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeleteModel = new ViewModelProvider(getActivity())
                .get(DeleteViewModel.class);
        mSettingsModel = new ViewModelProvider((getActivity())).get(SettingsViewModel.class);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDeleteBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //lamba expression example

        binding.button.setOnClickListener(this::attemptDelete);

        mDeleteModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeDeletionResponse);
    }

    private void attemptDelete(View view) {
        mDeleteModel.connect(
                mSettingsModel.getEmail(),
                binding.editPassword1.getText().toString()
              );
    }
    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to DeleteViewModel.
     *
     * @param response the Response from the server
     */
    private void observeDeletionResponse(final JSONObject response) {
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
                SharedPreferences prefs =
                        getActivity().getSharedPreferences(
                                getString(R.string.keys_shared_prefs),
                                Context.MODE_PRIVATE);
                prefs.edit().remove(getString(R.string.keys_prefs_jwt)).apply();
                //End the app completely
                PushyTokenViewModel model = new ViewModelProvider(this)
                        .get(PushyTokenViewModel.class);
                //when we hear back from the web service quit
                model.addResponseObserver(this, result -> getActivity().finishAndRemoveTask());
                    getActivity().finish();

            }
        } else {
            Log.d("JSON Response", "No Response");
        }

    }
}
