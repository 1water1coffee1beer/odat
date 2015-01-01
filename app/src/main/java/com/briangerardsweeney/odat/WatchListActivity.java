package com.briangerardsweeney.odat;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.briangerardsweeney.odat.util.GcmRegistrationAsyncTask;
import com.briangerardsweeney.odat.util.OptionsMenuHandler;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;


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
        implements WatchListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private static final String ACCOUNT_NAME = "accountName";
    private static final String SETTINGS = "deal-daemon-3-prefs";
    private String accountName;
    private SharedPreferences settings;
    GoogleAccountCredential credential;

    private static final int REQUEST_ACCOUNT_PICKER = 2;

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

        this.settings = getSharedPreferences(SETTINGS, 0);
        this.credential = GoogleAccountCredential.usingAudience(this, "server:client_id:646330062931-5nrl3bfiong5l1ik0tfnpcv85hvgs5n2.apps.googleusercontent.com");

        setSelectedAccountName(settings.getString(ACCOUNT_NAME, null));

        if(credential.getSelectedAccountName() != null){
            //run app

        } else {
            //make them sign in
            startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
        }

        //TODO add this to first run - dont run every time unless needed
        new GcmRegistrationAsyncTask(this).execute();

        // TODO: If exposing deep links into your app, handle intents here.
    }

    private void setSelectedAccountName(String accountName) {
        SharedPreferences.Editor editor = this.settings.edit();
        editor.putString(ACCOUNT_NAME, accountName);
        editor.commit();
        this.credential.setSelectedAccountName(accountName);
        this.accountName = accountName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_ACCOUNT_PICKER:
                if(data != null && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if(accountName!=null){
                        setSelectedAccountName(accountName);
                        SharedPreferences.Editor editor = this.settings.edit();
                        editor.putString(ACCOUNT_NAME, accountName);
                        editor.commit();
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
        if(result){
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
