package com.smartwallet.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.smartwallet.android.http.ServiceRequest;
import com.smartwallet.android.http.ServiceRequestListener;
import com.smartwallet.android.services.ClientManager;
import com.smartwallet.android.utils.UserManagement;

public class AddingBankActivity extends AppCompatActivity implements ServiceRequestListener {
    private EditText bankCode;
    private EditText bankIban;
    private CardView addButton;
    private ClientManager clientManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_bank);

        clientManager = ClientManager.getInstance();

        bankCode = (EditText) findViewById(R.id.bankCode);
        bankIban = (EditText) findViewById(R.id.bankIban);

        addButton = (CardView) findViewById(R.id.addBankAccount);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addButton.setClickable(false);

                String code = bankCode.getText().toString();
                String iban = bankIban.getText().toString();

                JSONObject json = new JSONObject();
                try {
                    json.put("accountNumber", iban);
                    json.put("token", code);
                    json.put("username", UserManagement.GetUsername(AddingBankActivity.this));
                } catch(JSONException e) {
                    System.err.println("JSON exception in adding bank activity");
                    e.printStackTrace();
                }

                ServiceRequest serviceRequest = new ServiceRequest(AddingBankActivity.this);
                serviceRequest.doPostJsonRequest("addAccountNumber", json);


            }
        });
    }

    @Override
    public void onResponse(int responseCode, String responseString) {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<String, Object>();
        map = gson.fromJson(responseString, map.getClass());
        boolean success = (boolean) map.get("success");

        if (success) {
            String accountNumber = bankIban.getText().toString();
            getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("accountNumber", accountNumber ).apply();
            getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("bic", accountNumber.substring(4,8) ).apply();
            Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_LONG).show();
            ClientManager.accountNumber = accountNumber;
            Intent myIntent = new Intent(AddingBankActivity.this, SuperwalletMainActivity.class);
            AddingBankActivity.this.startActivity(myIntent);
        }
        else {
            String message = (String) map.get("message");
            Toast.makeText(getApplicationContext(), "ERROR: " + message, Toast.LENGTH_LONG).show();
        }
        addButton.setClickable(true);
    }
}
