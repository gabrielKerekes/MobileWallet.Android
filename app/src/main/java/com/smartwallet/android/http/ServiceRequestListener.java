package com.smartwallet.android.http;

/**
 * Created by GabrielK on 17-Apr-17.
 */

public interface ServiceRequestListener {
    void onResponse(int responseCode, String responseString);
}
