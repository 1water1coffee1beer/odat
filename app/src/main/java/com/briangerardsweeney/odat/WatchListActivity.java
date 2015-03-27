package com.briangerardsweeney.odat;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.briangerardsweeney.odat.util.GcmRegistrationAsyncTask;
import com.briangerardsweeney.odat.util.RegistrationAsyncTask;
import com.briangerardsweeney.odat.util.OptionsMenuHandler;
import com.briangerardsweeney.odat.watchservice.registration.model.RegistrationRecord;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.concurrent.ExecutionException;

import static android.view.View.OnClickListener;


/**
 * An activity representing a list of Watches. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link WatchDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link WatchListFragment} and the item details
 * (if present) is a {@link WatchDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link WatchListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class WatchListActivity extends ActionBarActivity
        implements WatchListFragment.Callbacks, ConnectionCallbacks, OnConnectionFailedListener {

    public static final String PREF_ACCOUNT_NAME = "PREF_ACCOUNT_NAME";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private static final int RC_SIGN_IN = 0;

    static final int REQUEST_ACCOUNT_PICKER = 2;

    private GoogleApiClient mGoogleApiClient;

    private boolean mIntentInProgress;

    private RegistrationRecord registeredUser;

    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;

    private SharedPreferences settings;

    private static final String SETTINGS = "Deal Daemon Prefs";

    private GoogleAccountCredential credential;

    private String accountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_list);

        if (findViewById(R.id.watch_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((WatchListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.watch_list))
                    .setActivateOnItemClick(true);
        }

        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!WatchListActivity.this.mGoogleApiClient.isConnecting()) {
                    mSignInClicked = true;
                    resolveSignInError();
                }
            }
        });

        this.settings = getSharedPreferences(SETTINGS, 0);
        this.credential = GoogleAccountCredential.usingAudience(this, "server:client_id:646330062931-19sroourvuq8c9ecdajs7j8d1jjp6ila.apps.googleusercontent.com");
        setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        RegistrationAsyncTask task = new RegistrationAsyncTask(this, this.credential);
        //TODO idunno if this is good enough here???

        if(credential.getSelectedAccountName() != null) {
            //todo start app
            task.execute();
            try {
                this.registeredUser = task.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            this.chooseAccount();
        }

        //register("", "");    //TODO something...

        // TODO: If exposing deep links into your app, handle intents here.
    }

    private void setSelectedAccountName(String accountName) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.commit();
        credential.setSelectedAccountName(accountName);
        this.accountName = accountName;
    }

    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO check for availability of play services
        //see here: https://developer.android.com/google/play-services/setup.html

        int answer = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (answer != ConnectionResult.SUCCESS) {
            android.app.Dialog update = GooglePlayServicesUtil.getErrorDialog(answer, this, RC_SIGN_IN);
            update.show();
        }
    }

    private void register(String personName, String emailAddress) {
        RegistrationAsyncTask task = new RegistrationAsyncTask(this, this.credential);
        task.execute();

        try {
            RegistrationRecord registrationRecord = task.get();
            registrationRecord.getUser().setName(personName);
            registrationRecord.getUser().setEmail(emailAddress);

            this.registeredUser = registrationRecord;

            Toast.makeText(this, "User is connected " + this.registeredUser.getUser().getName(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resolveSignInError() {
        if (this.mConnectionResult.hasResolution()) {
            try {
                this.mIntentInProgress = true;
                IntentSender signInIntent = this.mConnectionResult.getResolution().getIntentSender();
                startIntentSenderForResult(signInIntent,
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                this.mIntentInProgress = false;
                this.mGoogleApiClient.connect();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();    //TODO remove
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();    //TODO remove
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (this.mGoogleApiClient.isConnected()) {
            this.mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:

                if (resultCode != RESULT_OK) {
                    mSignInClicked = false;
                }

                this.mIntentInProgress = false;

                if (!this.mGoogleApiClient.isConnecting()) {
                    this.mGoogleApiClient.connect();
                }
            case REQUEST_ACCOUNT_PICKER:
                if (data != null && data.getExtras() != null) {
                    String accountName =
                            data.getExtras().getString(
                                    AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        setSelectedAccountName(accountName);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                        // User is authorized.
                    }
                }
                break;
        }
    }

    /**
     * Callback method from {@link WatchListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(WatchDetailFragment.ARG_ITEM_ID, id);
            WatchDetailFragment fragment = new WatchDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.watch_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, WatchDetailActivity.class);
            detailIntent.putExtra(WatchDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.watch_list_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = new OptionsMenuHandler(this, item).invoke();
        if (result) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        this.mSignInClicked = false;

        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        String personName = currentPerson.getDisplayName();
        String emailAddress = Plus.AccountApi.getAccountName(this.mGoogleApiClient);

        //TODO do stuff with user data
    }

    @Override
    public void onConnectionSuspended(int i) {
        this.mGoogleApiClient.connect();
    }

    public void onConnectionFailed(ConnectionResult result) {

        Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();         //TODO remove

        if (!this.mIntentInProgress) {
            this.mConnectionResult = result;

            if (this.mSignInClicked) {
                this.resolveSignInError();
            }
        }
    }
}
