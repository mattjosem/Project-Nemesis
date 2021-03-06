package com.example.TCSS450GROUP1.ui.connections.sharanjit;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.TCSS450GROUP1.io.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author sharanjitsingh
 */
public class ConnectionListViewModel extends AndroidViewModel {
    private MutableLiveData<JSONObject> mContacts;

    public ConnectionListViewModel(@NonNull Application application) {
        super(application);
        mContacts = new MutableLiveData<>();
        mContacts.setValue(new JSONObject());
    }
    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer){
        mContacts.observe(owner, observer);
    }

    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            try {
                mContacts.setValue(new JSONObject("{" +
                        "error:\"" + error.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset())
                    .replace('\"', '\'');
            try {
                mContacts.setValue(new JSONObject("{" +
                        "code:" + error.networkResponse.statusCode +
                        ", data:\"" + data +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mModel = new ViewModelProvider(getActivity()).get(ContactListViewModel.class);
//        if (getActivity() instanceof MainActivity){
//            MainActivity activity = (MainActivity) getActivity();
//            //  mModel.setUserInfoViewModel(activity.getUserInfoViewModel());
//        }
//        mModel.connectGet();
//
//    }






    public void connect(String email, final String jwt) {



        String url = "https://team1-database.herokuapp.com/contacts/" + email;
        JSONObject body = new JSONObject();


        Log.i("Made it:", body.toString());
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                body,
                mContacts::setValue,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        Log.i("Made it:", request.toString());
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);


    }

    public void connectGet() {
//         mModel.connect(mUserModel.getEmail(), mUserModel.getJWT());
        //      binding.usernameTextView.setText(args.getContact().getUserName());
////        binding.firstnameTextView.setText(args.getContact().getFirstName());
////        binding.lastnameTextView.setText(args.getContact().getLastName());
//
    }
}


