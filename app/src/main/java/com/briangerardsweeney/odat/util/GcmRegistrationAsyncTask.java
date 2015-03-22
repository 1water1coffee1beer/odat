package com.briangerardsweeney.odat.util;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.briangerardsweeney.odat.watchservice.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Brian on 12/30/2014.
 * From here: https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints#22-registering-devices-with-google-cloud-messaging-backend
 */
public class GcmRegistrationAsyncTask extends AsyncTask<Void, Context, String> {
    private static Registration regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;

    private Credential credential;

    private static final String SENDER_ID = "646330062931";     //console project number

    public GcmRegistrationAsyncTask(Context context, Credential credential) {
        this.context = context;
        this.credential = credential;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (regService == null) {
            Registration.Builder builder =
                    new Registration.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(),
                            null)
                    .setRootUrl("https://com-briangerardsweeney-odat.appspot.com/_ah/api/");

            regService = builder.build();
        }

        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            String regId = gcm.register(SENDER_ID);
            msg = "Device registered, registration ID=" + regId;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            regService.register(regId).execute();

        } catch (IOException ex) {
            ex.printStackTrace();
            msg = "Error: " + ex.getMessage();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
    }
}
