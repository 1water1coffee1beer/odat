package com.briangerardsweeney.odat.util;

import android.content.Context;
import android.os.AsyncTask;

import com.briangerardsweeney.odat.watchservice.registration.Registration;
import com.briangerardsweeney.odat.watchservice.registration.model.RegistrationRecord;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.IOException;

/**
 * Created by Brian on 3/24/2015.
 */
public class RegistrationAsyncTask extends AsyncTask<String, Context, RegistrationRecord> {

    private static Registration regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;

    private String regId;

    private static final String SENDER_ID = "646330062931";     //console project number

    public RegistrationRecord registrationRecord;

    public GoogleAccountCredential credential;

    public RegistrationAsyncTask(Context context, GoogleAccountCredential credential) {
        this.context = context;
        this.credential = credential;
    }

    @Override
    protected void onPostExecute(RegistrationRecord registrationRecord) {
        this.registrationRecord = registrationRecord;
    }

    @Override
    protected RegistrationRecord doInBackground(String... params) {

        if (this.regService == null) {
            Registration.Builder builder =
                    new Registration.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(),
                            this.credential)
                            .setRootUrl("https://com-briangerardsweeney-odat.appspot.com/_ah/api/");

            this.regService = builder.build();
        }

        try {
            if (this.gcm == null) {
                this.gcm = GoogleCloudMessaging.getInstance(this.context);
            }
            String regId = this.gcm.register(SENDER_ID);
            this.regId = regId;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            //TODO authenticate
            //regService.register(regId).execute();

            RegistrationRecord registeredUser = regService.registerAndProvideRegistrationRecord(regId).execute();

            return registeredUser;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
