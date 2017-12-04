package com.smartwallet.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.smartwallet.android.adapters.PaymentAdapter;
import com.smartwallet.android.services.ClientManager;

import static com.smartwallet.android.R.id.balanceText;

public class BankAccountActivity extends AppCompatActivity {

    TextView balance;
    TextView accountNumberText;
    private RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ClientManager clientManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Bank Account");

        clientManager = ClientManager.getInstance();

        balance = (TextView)findViewById(balanceText);
        balance.setText(String.valueOf(ClientManager.balance));

        accountNumberText = (TextView)(findViewById(R.id.accountNumber));
        accountNumberText.setText(ClientManager.accountNumber);

        mRecyclerView = (RecyclerView) findViewById(R.id.paymant_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new PaymentAdapter(clientManager.getPayments());

        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_products:
                Intent myIntent = new Intent(BankAccountActivity.this, ProductActivity.class);
                BankAccountActivity.this.startActivity(myIntent);
                break;

            case R.id.action_boughtProducts:
                Intent boughtProductsIntent = new Intent(BankAccountActivity.this, BoughtProductsActivity.class);
                BankAccountActivity.this.startActivity(boughtProductsIntent);
                break;

            case R.id.action_shops:
                Intent myIntent2 = new Intent(BankAccountActivity.this, TopicActivity.class);
                BankAccountActivity.this.startActivity(myIntent2);
                break;

            case R.id.action_serverInfo:
                Intent serverIntent = new Intent(BankAccountActivity.this, ServerInfoActivity.class);
                BankAccountActivity.this.startActivity(serverIntent);
                break;

            case R.id.action_notifications:
                Intent notificationsIntent = new Intent(BankAccountActivity.this, NotificationListActivity.class);
                BankAccountActivity.this.startActivity(notificationsIntent);
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        BankAccountActivity.this.finish();
        return true;
    }
}
