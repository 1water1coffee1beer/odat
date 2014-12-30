package com.briangerardsweeney.odat.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.briangerardsweeney.odat.R;
import com.briangerardsweeney.odat.WatchListActivity;

/**
* Created by Brian on 12/30/2014.
*/
public class OptionsMenuHandler {
    private MenuItem item;
    private Activity activity;

    public OptionsMenuHandler(Activity activity, MenuItem item) {
        this.activity = activity;
        this.item = item;
    }

    private void test(String text){
        Context c = this.activity.getApplicationContext();
        CharSequence t = text;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(c, t, duration);
        toast.show();
    }

    public boolean invoke() {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpTo(this.activity, new Intent(this.activity, WatchListActivity.class));
                return true;
            case R.id.action_about:
                test("Test about");
                return true;
            case R.id.action_delete:
                test("Test delete");
                return true;
            case R.id.action_help:
                test("Test help");
                return true;
            case R.id.action_history:
                test("Test history");
                return true;
            case R.id.action_save:
                test("Test save");
                return true;
            case R.id.action_settings:
                test("Test settings");
                return true;
            case R.id.action_search:
                test("Test search");
                return true;
            default:
                return false;
        }
    }
}
