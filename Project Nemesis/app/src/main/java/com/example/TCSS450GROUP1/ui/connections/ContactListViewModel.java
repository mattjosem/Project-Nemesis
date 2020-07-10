package com.example.TCSS450GROUP1.ui.connections;

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
import com.example.TCSS450GROUP1.model.UserInfoViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Joseph Rushford
 * ContactList fragment view model to handle the call to the database to recieve the list of contacts
 */
public class ContactListViewModel extends AndroidViewModel {
    private MutableLiveData<List<Contacts>> mContacts;
    private UserInfoViewModel userInfoViewModel;

    public ContactListViewModel(@NonNull Application application) {
        super(application);
        mContacts = new MutableLiveData<>();
        mContacts.setValue(new ArrayList<>());
    }

    /**
     * Observer for the list of Contacts
     * @author Joseph Rushford
     * @param owner of the observer
     * @param observer to see if the contact list changed
     *
     */
    public void addContactListObserver(@NonNull LifecycleOwner owner,
                                         @NonNull Observer<? super List<Contacts>> observer) {
        mContacts.observe(owner, observer);
    }
    /**
     * Method to handle volley errors.
     * @param error VolleyError object.
     */
    private void handleError(final VolleyError error) {
        if (error != null && error.getMessage() != null) {
            Log.e("CONNECTION ERROR", error.getMessage());
            throw new IllegalStateException(error.getMessage());
        }
    }

    /**
     * @author Joseph Rushford
     * method to handle the result from the database call
     * @param result of the database call
     */
    private void handleResult(final JSONObject result) {
        Log.i("contacts", result.toString());
        if (!result.has("values")) {
            throw new IllegalStateException("Unexpected response in ContactsViewModel: " + result);
        }
        try {
            ArrayList<Contacts> contacts = new ArrayList<>();

            JSONArray jsonUsers = result.getJSONArray("values");
            for(int i = 0; i < jsonUsers.length(); i++) {
                JSONObject user = (new JSONObject(jsonUsers.getString(i)));
                contacts.add(new Contacts(user.getString("email"), i));
                }
            mContacts.setValue(contacts);
            Log.d("CONTACTS", "" + mContacts.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * connects to the database and recievers a list of contacts for the current user.
     * @param email the email of the current user
     * @param jwt the auth token
     */
    public void connect(final String email, final String jwt) {



        String url = "https://team1-database.herokuapp.com/contacts/" + email;
        JSONObject body = new JSONObject();


        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                body,
                this::handleResult,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };
        Log.i("contacts", url);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);


    }

}
