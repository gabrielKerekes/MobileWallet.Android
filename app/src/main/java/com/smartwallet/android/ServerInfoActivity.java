package com.smartwallet.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.smartwallet.android.services.ClientManager;

public class ServerInfoActivity extends AppCompatActivity {

    ClientManager clientManager;

    Button toggleConnection;
    EditText editPort;
    EditText editIp;

    String ip;
    String port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_info);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Server info");

        toggleConnection = (ToggleButton)findViewById(R.id.toggleButton);

        editIp = (EditText)findViewById(R.id.editIP);
        editPort = (EditText)findViewById(R.id.editPort);

        clientManager = ClientManager.getInstance();
        clientManager.setContext(getApplicationContext());
        if (ClientManager.connected) {
            toggleConnection.setText("Disconnect");
        } else {
            toggleConnection.setText("Connect");
        }

        toggleConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ClientManager.connected) {
                    getIpAndPort();
                    clientManager.setClient(clientManager.createClient(getApplicationContext(),ip,port));
                    clientManager.setCallbacs();
                    clientManager.connect();
                    toggleConnection.setText("Disconnect");
                }
                else {
                    clientManager.disconnect();
                    toggleConnection.setText("Connect");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (ClientManager.connected) {
//            toggleConnection.setText("Disconnect");
//            toggleConnection.setActivated(true);
//        } else {
//            toggleConnection.setText("Connect");
//            toggleConnection.setActivated(false);
//        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_products:
                Intent myIntent = new Intent(ServerInfoActivity.this, ProductActivity.class);
                ServerInfoActivity.this.startActivity(myIntent);
                break;

            case R.id.action_shops:
                Intent myIntent2 = new Intent(ServerInfoActivity.this, TopicActivity.class);
                ServerInfoActivity.this.startActivity(myIntent2);
                break;

            case R.id.action_bankAccount:
                Intent myIntent3 = new Intent(ServerInfoActivity.this, BankAccountActivity.class);
                ServerInfoActivity.this.startActivity(myIntent3);
                break;

            case R.id.action_boughtProducts:
                Intent boughtProductsIntent = new Intent(ServerInfoActivity.this, BoughtProductsActivity.class);
                ServerInfoActivity.this.startActivity(boughtProductsIntent);
                break;

            case R.id.action_notifications:
                Intent notificationsIntent = new Intent(ServerInfoActivity.this, NotificationListActivity.class);
                ServerInfoActivity.this.startActivity(notificationsIntent);
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        ServerInfoActivity.this.finish();
        return true;
    }

    public void getIpAndPort() {
        ip = editIp.getText().toString();
        port = editPort.getText().toString();
    }


}
