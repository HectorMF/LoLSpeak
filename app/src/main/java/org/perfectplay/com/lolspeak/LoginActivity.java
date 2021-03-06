package org.perfectplay.com.lolspeak;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.util.*;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;


/**
 * A login screen that offers login via email/password.

 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor>, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private final String TAG = ((Object) this).getClass().getSimpleName();
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    //Location
    private LocationClient mLocationClient;
    private Location mLocation;
    private Spinner mRegion;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ConnectionConfiguration config;
    public static XMPPTCPConnection xmppConnection;
    public static Roster roster;
    public static HashMap<String, ArrayList<Spanned>> messages = new HashMap<String, ArrayList<Spanned>>();
    private static boolean attached = false;

    public static Typeface font;
    public static PacketListener listener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            Message message = (Message) packet;
            if (message.getBody() != null) {
                String fromSimplified = message.getFrom().substring(0, message.getFrom().indexOf("/"));
                String fromName = roster.getEntry(fromSimplified).getName();
                if (!messages.containsKey(fromSimplified))
                    messages.put(fromSimplified, new ArrayList<Spanned>());
                messages.get(fromSimplified).add(Html.fromHtml("<font color='blue'><b>" + fromName + "</b></font>: " + message.getBody()));
            }
        }
    };

    //GOOGLE PLAY SEVICES STUFF ----- SNIPPETS FROM developer.android.com >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again?
                     */

                        break;
                }
        }
    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason.
            // resultCode holds the error code.
        } else {
            return false;
        }
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        mLocation = mLocationClient.getLastLocation();

        double lat = mLocation.getLatitude();
        double lon = mLocation.getLongitude();

        if(lat < -90)
        {
            if(lon > 45 && lon < 75)
                mRegion.setSelection(7);
        }
        else if(lat < -60)
        {
            if(lon < 45 && lon  > 30)
                mRegion.setSelection(6);
        }
        else if(lat < -30)
        {
            if(lon > 30 && lon < 75)
                mRegion.setSelection(2);
        }
        else if(lat < 30)
        {
            if(lon < 60 && lon > 30)
                mRegion.setSelection(1);
        }
        else if(lat < 60)
        {
            if(lon > -30 && lon < 0)
                mRegion.setSelection(5);
            if(lon < 15 && lon > -20)
                mRegion.setSelection(8);
            if(lon < -20 && lon > -60)
                mRegion.setSelection(9);
        }
        else if(lat < 90)
        {
            if(lon < 15 && lon > -20)
                mRegion.setSelection(8);
            if(lon < -20 && lon > -60)
                mRegion.setSelection(9);
            if(lon < 75 && lon > 30)
                mRegion.setSelection(0);
        }
        else if(lat < 120)
        {
            if(lon < 75 && lon > 30)
                mRegion.setSelection(0);
        }
        else
        {
            mRegion.setSelection(4);
        }
    }

    @Override
    public void onDisconnected() {
        // Display the connection status
        //Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //Do nothing, no location available so use default default in server spinner
        }
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        font = Typeface.createFromAsset(getAssets(), "fonts/OSP-DIN.ttf");
        TextView text = (TextView) findViewById(R.id.logo);
        text.setTypeface(font);
        text = (TextView) findViewById(R.id.logo2);
        text.setTypeface(font);
        int id = Resources.getSystem().getIdentifier("btn_check_holo_dark", "drawable", "android");
        ((CheckBox) findViewById(R.id.rememberPassword)).setButtonDrawable(id);

        mRegion = (Spinner)findViewById(R.id.regionSpinner);
        mLocationClient = new LocationClient(this, this, this);

        Log.e(TAG, "+++ In onCreate() +++");

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    protected void onResume(){
        super.onResume();
        Log.e(TAG, "++ In onResume() ++");
        // Rest of onResume()...
    }

    protected void onPause(){
        super.onResume();
        Log.e(TAG, "++ In onPause() ++");
        // Rest of onResume()...
    }

    protected void onStart(){
        super.onStart();
        mLocationClient.connect();

        Log.e(TAG, "++ In onStart() ++");
        // Rest of onResume()...
    }

    protected void onRestart(){
        super.onRestart();
        Log.e(TAG, "++ In onRestart() ++");
        // Rest of onResume()...
    }
    protected void onStop(){
        mLocationClient.disconnect();
        super.onStop();
        Log.e(TAG, "++ In onStop() ++");
        // Rest of onResume()...
    }

    protected void onDestroy(){
        super.onDestroy();
        Log.e(TAG, "++ In onDestroy() ++");
        // Rest of onResume()...
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(this, email, password);
            mAuthTask.execute((Void) null);
        }
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                                                                     .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private Activity activity;


        UserLoginTask(Activity activity, String email, String password) {
            this.activity = activity;
            mEmail = email;
            mPassword = password;
        }

        private String idToServer(int id) {
            if(id == 0)
                return "chat.na2.lol.riotgames.com";
            if(id == 1)
                return "chat.euw1.lol.riotgames.com";
            if(id == 2)
                return "chat.eun1.riotgames.com";
            if(id == 3)
                return "chat.pbe1.lol.riotgames.com";
            if(id == 4)
                return "chat.oc1.lol.riotgames.com";
            if(id == 5)
                return "chat.br.lol.riotgames.com";
            if(id == 6)
                return "chat.tr.lol.riotgames.com";
            if(id == 7)
                return "chat.ru.lol.riotgames.com";
            if(id == 8)
                return "chat.la1.lol.riotgames.com";
            if(id == 9)
                return "chat.la2.lol.riotgames.com";

            return "chat.na2.lol.riotgames.com";
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Log.d("LoginActivity", "Attempting to Login to server: " + idToServer((int)mRegion.getSelectedItemId()));
            config = new ConnectionConfiguration(idToServer((int)mRegion.getSelectedItemId()), 5223, "pvp.net");
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
            config.setSocketFactory(new DummySSLSocketFactory());
            config.setCompressionEnabled(true);
            xmppConnection = new XMPPTCPConnection(config);

            try {
                xmppConnection.connect();
                xmppConnection.login(mEmail, "AIR_" + mPassword , "xiff");
                roster = xmppConnection.getRoster();
                    // Add a packet listener to get messages sent to us
                PacketFilter filter =  MessageTypeFilter.CHAT;
                xmppConnection.removePacketListener(listener);
                xmppConnection.addPacketListener(listener, filter);
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return xmppConnection.isAuthenticated();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                activity.startActivity(new Intent(activity, MainMenu.class));
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}



