package com.mobilewallet.android.avengerstoken;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.mobilewallet.android.R;
import com.mobilewallet.android.SplashScreenActivity;
import com.mobilewallet.android.services.ToastMaker;
import com.mobilewallet.android.utils.ServiceIp;

public class RegActivity extends AppCompatActivity {

    private View.OnClickListener callWSListener;
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    SharedPreferences sharedPreferences;

    GoogleCloudMessaging gcm;
    String regid = null;
    Context context;

    private String data;
    private int responseCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        gcm = GoogleCloudMessaging.getInstance(this);
        context = getApplicationContext();

        callWSListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //parseAndSend(v);
                registerInBackground();
            }
        };

        Button okButt = (Button)findViewById(R.id.okbutt);
        okButt.setOnClickListener(callWSListener);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);

        sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reg, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    public void parseAndSend(){

        EditText editUserName = (EditText) findViewById(R.id.unameTttttt);
        EditText editPassword = (EditText) findViewById(R.id.pwdT);
        EditText editRepeatedPassword = (EditText) findViewById(R.id.rep_pwdT);
        EditText editMail = (EditText) findViewById(R.id.mail);
        EditText editRepeatedMail = (EditText) findViewById(R.id.repeatedMail);
        EditText editPin = (EditText) findViewById(R.id.pinT);

        String uname = editUserName.getText().toString();
        String pwd = editPassword.getText().toString();
        String rep_pwd = editRepeatedPassword.getText().toString();
        final String mail = editMail.getText().toString();
        String repeatedMail = editRepeatedMail.getText().toString();
        String pin = editPin.getText().toString();

        if(uname.isEmpty() || pwd.isEmpty() || rep_pwd.isEmpty() || mail.isEmpty() || repeatedMail.isEmpty() || pin.isEmpty()){
            ToastMaker.makeToast("All fields are required!", getApplicationContext());
        } else if (!mail.equals(repeatedMail)) {
            ToastMaker.makeToast("E-mails are not the same!", getApplicationContext());
        } else if (!pwd.equals(rep_pwd)) {
            ToastMaker.makeToast("Passwords are not the same!", getApplicationContext());
        } else if (pin.length() != 4) {
            ToastMaker.makeToast("Pin must be 4-digit long!", getApplicationContext());
        } else {

            String imei = sharedPreferences.getString("imei","");

            if(regid == null){
                Toast.makeText(context, "GCM Error", Toast.LENGTH_LONG).show();
            }else {
                String data = uname + ":" + pwd + ":" + rep_pwd + ":" + mail + ":" + repeatedMail + ":" + pin + ":" + imei + ":" + regid;
                String[] params = {data, pin, uname};
//            Service service = new Service(data, this, pin, uname, regid);
//            service.execute();
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        String data = params[0];
                        String pin = params[1];
                        String uname = params[2];

                        HttpsURLConnection urlConnection = null;
                        //HttpURLConnection urlConnection = null;
                        try {
                            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                                public X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }

                                @Override
                                public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
                                    // Not implemented
                                }

                                @Override
                                public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
                                    // Not implemented
                                }
                            } };

                            SSLContext sc = SSLContext.getInstance("TLS");
                            sc.init(null, trustAllCerts, new java.security.SecureRandom());
                            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());

                            URL url = new URL(ServiceIp.GetIp(context) + "/regDevice?data="+data);

                            urlConnection = (HttpsURLConnection)url.openConnection();
                            InputStream in = urlConnection.getInputStream();
                            BufferedReader r = new BufferedReader(new InputStreamReader(in));

                            StringBuilder total = new StringBuilder();
                            String line;
                            while ((line = r.readLine()) != null) {
                                total.append(line);
                            }
                            String message = total.toString();

                            JSONObject json = new JSONObject(message);
                            String result = json.getString("response");

                            switch (result){
                                case "suc":
                                    responseCode = 201;
                                    String grid = json.getString("data");

                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    editor.putString("grid",grid);
                                    editor.putString("pin",pin);
                                    editor.putString("uname",uname);
                                    editor.putString("email",mail);
                                    editor.apply();

                                    SharedPreferences gcm_pref = context.getSharedPreferences(Globals.PREFS_NAME, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor gcm_edit = gcm_pref.edit();
                                    gcm_edit.putString(Globals.PREFS_PROPERTY_REG_ID, regid);
                                    gcm_edit.apply();
                                    break;
                                case "fail":
                                    responseCode = 417;
                                    break;
                                case "err":
                                    responseCode = 409;
//                                err_mess = json.getString("data");
                                    break;
                                case "exc":
                                    responseCode = 500;
                                    break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (urlConnection != null) {
                                urlConnection.disconnect();
                            }
                        }

                        return "sup";
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        evaluateResponse(responseCode);
                    }

                }.execute(params);
            }

        }

    }

    private void evaluateResponse(int resp){

        switch (resp) {
            case -1:
                Toast.makeText(context, "Nespecifikovana chyba", Toast.LENGTH_LONG).show();
                break;
            case 201:
                Toast.makeText(context, "Registration successful", Toast.LENGTH_LONG).show();
                Toast.makeText(context, "Device synchronized", Toast.LENGTH_LONG).show();
                Intent nextactivity = new Intent(RegActivity.this, SplashScreenActivity.class);
                startActivity(nextactivity);
                finish();
                break;
            case 417:
                Toast.makeText(context, "Username already exists", Toast.LENGTH_LONG).show();
                break;
            case 409:
//                err_mess = err_mess.replace("-","\n");
                Toast.makeText(context, "ERROR 409", Toast.LENGTH_LONG).show();
                break;
            case 500:
                Toast.makeText(context, "ERROR 500", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void registerInBackground()
    {
        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                String msg = "";
                try
                {
                    if (gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(Globals.GCM_SENDER_ID);

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                }
                catch (IOException ex)
                {
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg)
            {
                parseAndSend();
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId)
    {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(Globals.TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Globals.PREFS_PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private SharedPreferences getGcmPreferences(Context context)
    {
        // This sample app persists the registration ID in shared preferences,
        // but how you store the regID in your app is up to you.
        return getSharedPreferences(Globals.PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public class NullHostNameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            Log.i("RestUtilImpl", "Approving certificate for " + hostname);
            return true;
        }

    }
}
