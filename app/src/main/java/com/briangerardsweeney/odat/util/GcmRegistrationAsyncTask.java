package com.briangerardsweeney.odat.util;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.briangerardsweeney.odat.watchservice.registration.Registration;
import com.briangerardsweeney.odat.watchservice.registration.model.RegistrationRecord;
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
public class GcmRegistrationAsyncTask extends AsyncTask<Void, Context, RegistrationRecord> {
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
    protected RegistrationRecord doInBackground(Void... params) {
        if (regService == null) {
            Registration.Builder builder =
                    new Registration.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(),
                            null)
                    .setRootUrl("https://com-briangerardsweeney-odat.appspot.com/_ah/api/");

            regService = builder.build();
        }

        RegistrationRecord record = null;
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            String regId = gcm.register(SENDER_ID);

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            record = regService.registerAndReturnObject(regId).execute();
            //regService.register(regId).execute();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return record;
    }

    @Override
    protected void onPostExecute(RegistrationRecord record) {
        Toast.makeText(context, record.getRegId(), Toast.LENGTH_LONG).show();
        Logger.getLogger("REGISTRATION").log(Level.INFO, record.getRegId());
    }
}
