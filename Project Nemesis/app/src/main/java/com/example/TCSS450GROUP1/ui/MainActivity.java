package com.example.TCSS450GROUP1.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.TCSS450GROUP1.R;
import com.example.TCSS450GROUP1.databinding.ActivityMainBinding;
import com.example.TCSS450GROUP1.model.NewMessageCountViewModel;
import com.example.TCSS450GROUP1.model.PushyTokenViewModel;
import com.example.TCSS450GROUP1.model.UserInfoViewModel;
import com.example.TCSS450GROUP1.ui.chat.ChatMessage;
import com.example.TCSS450GROUP1.ui.chat.ChatViewModel;
import com.example.TCSS450GROUP1.ui.chat.PushReceiver;
import com.example.TCSS450GROUP1.ui.home.HomeViewModel;
import com.example.TCSS450GROUP1.ui.weather.LocationViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Activity where most fragments take place
 @author Joseph Rushford
 */
public class MainActivity extends AppCompatActivity {
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    // A constant int for the permissions request code. Must be a 16 bit number
    private static final int MY_PERMISSIONS_LOCATIONS = 8414;
    private LocationRequest mLocationRequest;
    //Use a FusedLocationProviderClient to request the location
    private FusedLocationProviderClient mFusedLocationClient;
    // Will use this call back to decide what to do when a location change is detected
    private LocationCallback mLocationCallback;
    //The ViewModel that will store the current location

//    private HomeViewModel mLocationModel;
    private HomeViewModel mHomeViewModel;
    private LocationViewModel mLocationModel;
    private AppBarConfiguration mAppBarConfiguration;
    private MainPushMessageReceiver mPushMessageReceiver;
    private NewMessageCountViewModel getmNewMessageModel;
    private ActivityMainBinding binding;
    private NewMessageCountViewModel mNewMessageModel;
    private SharedPreferences  mSharedTheme;
    private static final String THEME_KEY = "currentTheme";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(getSavedTheme());


        mSharedTheme = getSharedPreferences("currentTheme", MODE_PRIVATE);
        mNewMessageModel = new ViewModelProvider(this).get(NewMessageCountViewModel.class);
        //getTheme().applyStyle(R.style.OverlayThemePink, true);
        mSharedTheme.getString(THEME_KEY, "default");
        if(mSharedTheme.getString(THEME_KEY, "default").equals("default")) {
            getTheme().applyStyle(R.style.AppTheme, true);
        } else {
            getTheme().applyStyle(R.style.OverlayThemePink, true);
        }
        super.onCreate(savedInstanceState);
        //boolean theme = preferences.getBoolean("theme", false);
//
//        if(theme) {
//            setTheme(R.style.AppTheme_Dark_NoActionBar);
//       }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());
        String email = args.getEmail();

        new ViewModelProvider(this, new UserInfoViewModel.UserInfoViewModelFactory(args.getEmail(), args.getJwt(), "temp" )).get(UserInfoViewModel.class);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        //R.id.navigation_settings,
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home,
                R.id.navigation_chatold, R.id.navigation_connections, R.id.navigation_weather).build();
                //R.id.navigation_weather).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_chatold) {
                //When the user navigates to the chats page, reset the new message count.
                //This will need some extra logic for your project as it should have
                //multiple chat rooms.
                mNewMessageModel.reset();
            }
        });
        mNewMessageModel.addMessageCountObserver(this, count -> {
            BadgeDrawable badge = binding.navView.getOrCreateBadge(R.id.navigation_chatold);
            badge.setMaxCharacterCount(2);
            if (count > 0) {
                //new messages! update and show the notification badge.
                badge.setNumber(count);
                badge.setVisible(true);
            } else {
                //user did some action to clear the new messages, remove the badge
                badge.clearNumber();
                badge.setVisible(false);
            }
        });
        Log.d("made it here", "location should work");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        } else {
            //The user has already allowed the use of Locations. Get the current location.
            requestLocation();
        }
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    Log.d("LOCATION UPDATE!", location.toString());
                    if (mLocationModel == null) {

                        mLocationModel = new ViewModelProvider(MainActivity.this)
                                .get(LocationViewModel.class);
                    }
                    if(mHomeViewModel == null) {
                        mHomeViewModel = new ViewModelProvider(MainActivity.this)
                                .get(HomeViewModel.class);
                    }
                    mHomeViewModel.setLocation(location);
                    mLocationModel.setLocation(location);
                }
            };
        };

        createLocationRequest();
        startLocationUpdates();
        stopLocationUpdates();
        Log.d("made it here", "location call finished");

    }
    /**
     * Create and configure a Location Request used when retrieving location updates
     */
    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // locations-related task you need to do.
                    requestLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("PERMISSION DENIED", "Nothing to see or do here.");

                    //Shut down the app. In production release, you would let the user
                    //know why the app is shutting down...maybe ask for permission again?
                   // finishAndRemoveTask();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("REQUEST LOCATION", "User did NOT allow permission to request location!");
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.d("LOCATION", location.toString());
                                if (mLocationModel == null) {




                                    mLocationModel = new ViewModelProvider(MainActivity.this)
                                            .get(LocationViewModel.class);
                                }
                                if(mHomeViewModel == null) {
                                    mHomeViewModel = new ViewModelProvider(MainActivity.this)
                                            .get(HomeViewModel.class);
                                }
                                mHomeViewModel.setLocation(location);



                                mLocationModel.setLocation(location);

                            }
                        }
                    });
        }
    }
    /**
     * @author Joseph Rushford
     * @return navigation bar
     */
    @Override
    public boolean onSupportNavigateUp(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drop_down, menu);

        return true;
    }

    /**
     * Creates setting drop down menu with on clicklisteners
     * @author Joseph Rushford
     * @param item the item being clicked in the menu
     * @return which item is being clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                signOut();
                break;
            case R.id.action_settings:

                goToSettings();
                break;

            case R.id.action_defaultColor:
                mSharedTheme.edit().putString(THEME_KEY, "default").apply();
                recreate();
//                Intent intent= getIntent();
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                finish();
//                startActivity(intent);

              //  toggleTheme(false);
                break;
            case R.id.action_PINK:
                mSharedTheme.edit().putString(THEME_KEY, "pink").apply();
                recreate();
               // Intent intentPink = getIntent();
                //intentPink.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                //finish();
                //startActivity(intentPink);

              //  toggleTheme(true);
                break;
            default:
                super.onOptionsItemSelected(item);

        }
        return false;
    }
//
//

    /**
     * @author Joseph Rushford
     * used for the menu settings
     */
    private void goToSettings() {
            Log.i("settings:" , "should go to settings");
           Intent intent = new Intent(MainActivity.this, AccountActivity.class);
           ViewModelProvider test = new ViewModelProvider(this);
           UserInfoViewModel user = test.get(UserInfoViewModel.class);
           intent.putExtra("jwt",user.getJWT());
           intent.putExtra("email", user.getEmail());
            startActivity(intent);
    }



    /**
     * @author Joseph Rushford
     * helper method for menu item to log out user.
     */
    private void signOut() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        prefs.edit().remove(getString(R.string.keys_prefs_jwt)).apply();
        //End the app completely
        PushyTokenViewModel model = new ViewModelProvider(this)
                .get(PushyTokenViewModel.class);
        //when we hear back from the web service quit
        model.addResponseObserver(this, result -> finishAndRemoveTask());

        model.deleteTokenFromWebservice(
                new ViewModelProvider(this)
                        .get(UserInfoViewModel.class)
                        .getJWT()
        );
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReceiver == null) {
            mPushMessageReceiver = new MainPushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        registerReceiver(mPushMessageReceiver, iFilter);
        //startLocationUpdates();

    }
    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReceiver != null){
            unregisterReceiver(mPushMessageReceiver);
        }
        //topLocationUpdates();

    }
    /**
     * Requests location updates from the FusedLocationApi.
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }
    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * @author Joseph Rushford
     * A BroadcastReceiver that listens for messages sent from PushReceiver
     */
    private class MainPushMessageReceiver extends BroadcastReceiver {
        private ChatViewModel mModel =
                new ViewModelProvider(MainActivity.this)
                        .get(ChatViewModel.class);
        //private HomeViewModel mHomeModel = new ViewModelProvider(MainActivity.this).
             //   get(HomeViewModel.class);
        @Override
        public void onReceive(Context context, Intent intent) {
            NavController nc =
                    Navigation.findNavController(
                            MainActivity.this, R.id.nav_host_fragment);
            NavDestination nd = nc.getCurrentDestination();
            if (intent.hasExtra("chatMessage")) {
                ChatMessage cm = (ChatMessage) intent.getSerializableExtra("chatMessage");
                //If the user is not on the chat screen, update the
                // NewMessageCountView Model
                if (nd.getId() != R.id.navigation_chatold) {
                    mNewMessageModel.increment();

                }

                //mHomeModel.changeRecentChat(cm);

                //Inform the view model holding chatroom messages of the new
                //message.
                mModel.addMessage(intent.getIntExtra("chatid", -1), cm);
            }
        }
    }

}
