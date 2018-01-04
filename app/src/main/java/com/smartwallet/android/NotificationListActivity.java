package com.mobilewallet.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mobilewallet.android.adapters.NotificationAdapter;
import com.mobilewallet.android.services.SharedPreferencesHelper;

public class NotificationListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private CardView removeNotifications;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notifications");

        SharedPreferencesHelper.updateNotifications(this);

        recyclerView = (RecyclerView) findViewById(R.id.notification_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // ClientManager ma funkciu setTestingNotifications a tam sa nastavuju testovacie
        adapter = new NotificationAdapter(SharedPreferencesHelper.getAllNotifications(getApplicationContext()),getApplicationContext());
        recyclerView.setAdapter(adapter);

        removeNotifications = (CardView) findViewById(R.id.removeNotificationsButton);
        removeNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog().create().show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_shops:
                Intent topicIntent = new Intent(NotificationListActivity.this, TopicActivity.class);
                NotificationListActivity.this.startActivity(topicIntent);
                break;

            case R.id.action_bankAccount:
                Intent bankIntent = new Intent(NotificationListActivity.this, BankAccountActivity.class);
                NotificationListActivity.this.startActivity(bankIntent);
                break;

            case R.id.action_serverInfo:
                Intent serverIntent = new Intent(NotificationListActivity.this, ServerInfoActivity.class);
                NotificationListActivity.this.startActivity(serverIntent);
                break;

            case R.id.action_products:
                Intent productIntent = new Intent(NotificationListActivity.this, ProductActivity.class);
                NotificationListActivity.this.startActivity(productIntent);
                break;

            case R.id.action_boughtProducts:
                Intent boughtProductsIntent = new Intent(NotificationListActivity.this, BoughtProductsActivity.class);
                NotificationListActivity.this.startActivity(boughtProductsIntent);
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        NotificationListActivity.this.finish();
        return true;
    }

    AlertDialog.Builder createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Remove all notifications?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferencesHelper.removeAllNotifications(getApplicationContext());
                        recyclerView.setAdapter(null);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // canceled
                    }
                });
        // Create the AlertDialog object and return it
        return builder;

    }
}
