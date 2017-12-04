package com.smartwallet.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.smartwallet.android.avengerstoken.AvengersMainActivity;
import com.smartwallet.android.services.ClientManager;


public class SuperwalletMainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superwallet_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);

        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);

        String username = sharedPreferences.getString("uname", null);
        String email = sharedPreferences.getString("email", null);
        String accountNumber = sharedPreferences.getString("accountNumber", null);
        String bic = sharedPreferences.getString("bic", null);

        if (accountNumber == null) {
            Intent myIntent = new Intent(SuperwalletMainActivity.this, AddingBankActivity.class);
            SuperwalletMainActivity.this.startActivity(myIntent);
        }
        else {
            ClientManager.userName = username;
            ClientManager.email = email;
            ClientManager.accountNumber = accountNumber;
            ClientManager.BIC = bic;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_bankAccount:
                Intent myIntent = new Intent(SuperwalletMainActivity.this, BankAccountActivity.class);
                SuperwalletMainActivity.this.startActivity(myIntent);
                break;

            case R.id.action_shops:
                Intent shopIntent = new Intent(SuperwalletMainActivity.this, TopicActivity.class);
                SuperwalletMainActivity.this.startActivity(shopIntent);
                break;

            case R.id.action_boughtProducts:
                Intent boughtProductsIntent = new Intent(SuperwalletMainActivity.this, BoughtProductsActivity.class);
                SuperwalletMainActivity.this.startActivity(boughtProductsIntent);
                break;


            case R.id.action_products:
                Intent myIntent2 = new Intent(SuperwalletMainActivity.this, ProductActivity.class);
                SuperwalletMainActivity.this.startActivity(myIntent2);
                break;

            case R.id.action_token:
                Intent tokenIntent = new Intent(SuperwalletMainActivity.this, AvengersMainActivity.class);
                SuperwalletMainActivity.this.startActivity(tokenIntent);
                break;

            case R.id.action_notifications:
                Intent notificationIntent = new Intent(SuperwalletMainActivity.this, NotificationListActivity.class);
                SuperwalletMainActivity.this.startActivity(notificationIntent);
                break;


            case R.id.action_serverInfo:
                Intent serverIntent = new Intent(SuperwalletMainActivity.this, ServerInfoActivity.class);
                SuperwalletMainActivity.this.startActivity(serverIntent);
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        return true;
    }


}
