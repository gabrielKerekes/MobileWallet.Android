package com.smartwallet.android.http;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.smartwallet.android.utils.ServiceIp;

/**
 * Created by GabrielK on 22-Feb-17.
 */

public class ServiceRequest {
    private ServiceRequestListener listener;
    private Context context;

    public ServiceRequest(Context context) {
        this.context = context;
        if (context instanceof ServiceRequestListener) {
            listener = (ServiceRequestListener) context;
        }
    }

    public void doPostJsonRequest(String action, JSONObject data) {
        new AsyncTask<String, Void, String>() {
            private int responseCode;
            private String responseString;

            @Override
            protected String doInBackground(String... params) {
                HttpsURLConnection urlConnection = null;
                try {
                    String action = params[0];
                    String dataJsonString = params[1];

                    String url = ServiceIp.GetIp(context) + "/" + action;
                    urlConnection = createConnection("POST", "application/json", url);

                    writeDataObjectToUrlConnection(urlConnection, dataJsonString);

                    responseCode = urlConnection.getResponseCode();

                    responseString = readResponseFromUrlConnection(urlConnection);
                } catch (Exception e) {
                    Log.e("SW", "Service request exception:", e);
                    responseString = "{\"success\":false, \"message\":\"EXCEPTION\", \"exceptionMessage\":\"" + e.getMessage() + "\"}";
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

                return responseString;
            }

            @Override
            protected void onPostExecute(String msg) {
                onResponse(responseCode, responseString);
            }

        }.execute(action, data.toString());
    }

    private void writeDataObjectToUrlConnection(HttpsURLConnection urlConnection, String dataJsonString) throws IOException {
        OutputStream os = urlConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(dataJsonString);
        writer.flush();
        writer.close();
        os.close();
    }

    private String readResponseFromUrlConnection(HttpsURLConnection urlConnection) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }

        return sb.toString();
    }

    private void onResponse(int responseCode, String responseString) {
        listener.onResponse(responseCode, responseString);
    }

    private HttpsURLConnection createConnection(String requestMethod, String contentType, String urlString)
            throws NoSuchAlgorithmException, KeyManagementException, IOException
    {
        HttpsURLConnection urlConnection = null;

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager()
        {
            public X509Certificate[] getAcceptedIssuers()
            {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1)
            {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1)
            {
                // Not implemented
            }
        }};

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());

        URL url = new URL(urlString);

        //read data
        urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestMethod(requestMethod);
        urlConnection.setRequestProperty("Content-Type", contentType);

        return urlConnection;
    }

    public class NullHostNameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            Log.i("RestUtilImpl", "Approving certificate for " + hostname);
            return true;
        }
    }
}
