package com.example.TCSS450GROUP1.ui.chat;

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
 * @author Nick Caron
 */
public class AddChatViewModel extends AndroidViewModel {
    private MutableLiveData<JSONObject> mContacts;
    private int newChatID;
    private String mJwt;
    private String mEmail;
    private String mName;

    public AddChatViewModel(@NonNull Application application) {
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

    public void connectAddChat( final String jwt, final String email,final String chatName) {
        mJwt = jwt;
        mEmail = email;
        mName = chatName;
        String url = "https://team1-database.herokuapp.com/chats";
        JSONObject body = new JSONObject();
        try{
            body.put("name", chatName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Made it:", body.toString());
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                this::handelSuccess,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);


    }

    private void handelSuccess(final JSONObject response) {
        Log.i("Made it:", response.toString());
        try {
            newChatID = response.getInt("chatID");
        }catch(JSONException e){
            Log.e("Bad Chat:", e.toString());
        }

        //Try to add member to new chat
        String nexturl = "https://team1-database.herokuapp.com/chats/" + newChatID ;
        JSONObject nextbody = new JSONObject();
        //Log.i("Made it:", body.toString());
        Request newrequest = new JsonObjectRequest(
                Request.Method.PUT,
                nexturl,
                nextbody,
                null,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", mJwt);
                return headers;
            }
        };

        String addurl = "https://team1-database.herokuapp.com/chats/email" ;
        JSONObject addbody = new JSONObject();
        //Log.i("Made it:", body.toString());
        try{
            addbody.put("useremail", mEmail);
            addbody.put("chatID", newChatID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request addrequest = new JsonObjectRequest(
                Request.Method.POST,
                addurl,
                addbody,
                null,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", mJwt);
                return headers;
            }
        };





        Log.i("Made it:", newrequest.toString());
        newrequest.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(newrequest);
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(addrequest);

    }

}
