package com.example.treehole;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

import me.pushy.sdk.Pushy;

public class UserUtils {
    public static SharedPreferences userDetails;
    private static final String USER_PREF = "user_details";
    private static final String USERNAME = "USERNAME";
    private static UserUtils instance;

    private Boolean loginStatus = false;
    private Context context;

    private UserUtils(Context context) {
        this.context = context;
        userDetails = context.getSharedPreferences(USER_PREF, MODE_PRIVATE);
        loginStatus = userDetails.getBoolean("LOGIN_SIT", false);
    }

    public static UserUtils getInstance(Context context) {
        if (instance == null) {
            instance = new UserUtils(context);
        }
        return instance;
    }

    public static class RegisterForPushNotificationsAsync extends AsyncTask<Void, Void, Object> {
        Activity mActivity;
        String ALREADY_REGISTERED = "[ALREADY REGISTERED DEVICE TOKEN]";

        public RegisterForPushNotificationsAsync(Activity activity) {
            this.mActivity = activity;
        }

        protected Object doInBackground(Void... params) {
            try {
                String deviceToken = "";
                if (!Pushy.isRegistered(mActivity)) {
                // Register the device for notifications (replace MainActivity with your Activity class name)
                    deviceToken = Pushy.register(mActivity);
                } else{
                    return ALREADY_REGISTERED;
                }

                // Registration succeeded, log token to logcat
                Log.d("Pushy", "Pushy device token: " + deviceToken);

                // Send the token to your backend server via an HTTP GET request
                new URL("https://rickyvu.pythonanywhere.com/messaging/device?token=" + deviceToken).openConnection();

                // Provide token to onPostExecute()
                return deviceToken;
            }
            catch (Exception exc) {
                // Registration failed, provide exception to onPostExecute()
                return exc;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            String message;

            // Registration failed?
            if (result instanceof Exception) {
                // Log to console
                Log.e("Pushy", result.toString());

                // Display error in alert
                message = ((Exception) result).getMessage();
            }
            else {
                if (result.toString().equals(ALREADY_REGISTERED)){
                    message = "Pushy device token already registered";
                } else {
                    message = "Pushy device token: " + result.toString() + "\n\n(copy from logcat)";
                }

            }

            // DEBUG
            // Registration succeeded, display an alert with the device token
            new android.app.AlertDialog.Builder(this.mActivity)
                    .setTitle("Pushy")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }

    public void setLogIn(Boolean val){
        if (!val){
            clearUser();
        }
        loginStatus = val;
        SharedPreferences.Editor editor = userDetails.edit();
        editor.putBoolean("LOGIN_SIT",val);
        editor.apply();
    }

    public Boolean isLoggedIn(){
        return loginStatus;
    }


    public void jumpToLogin() {
        /*// Get the instance of the FragmentManager
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();;

        // Find the target fragment
        LoginFragment targetFragment = (LoginFragment) fragmentManager.findFragmentById(R.id.fragment_container);

        // If the target fragment is not found, create a new instance of the fragment
        if (targetFragment == null) {
            targetFragment = new LoginFragment();
        }

        if (!fragmentManager.isStateSaved()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, targetFragment)
                    .addToBackStack(null)
                    .commit();
        }*/
        /*ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // Handle the result here
                    }
                });

        // Start the target activity using the launcher
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        launcher.launch(intent);*/
        //Intent intent = new Intent(context, LoginActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //context.startActivity(intent);
    }

    public void clearUser(){
        SharedPreferences.Editor editor = userDetails.edit();
        editor.clear();
        editor.apply();
    }
}
