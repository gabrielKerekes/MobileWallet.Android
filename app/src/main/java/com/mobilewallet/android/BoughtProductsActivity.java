package com.mobilewallet.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mobilewallet.android.adapters.BoughtProductAdapter;
import com.mobilewallet.android.services.ClientManager;

public class BoughtProductsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private BoughtProductAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ClientManager clientManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bought_products);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        clientManager = ClientManager.getInstance();

        getSupportActionBar().setTitle("Bought Products");

        mRecyclerView = (RecyclerView) findViewById(R.id.boughtProducts_recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new BoughtProductAdapter(clientManager.getBoughtProducts());

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

            case R.id.action_shops:
                Intent myIntent = new Intent(BoughtProductsActivity.this, TopicActivity.class);
                BoughtProductsActivity.this.startActivity(myIntent);
                break;

            case R.id.action_products:
                Intent productsIntent = new Intent(BoughtProductsActivity.this, ProductActivity.class);
                BoughtProductsActivity.this.startActivity(productsIntent);
                break;

            case R.id.action_bankAccount:
                Intent myIntent2 = new Intent(BoughtProductsActivity.this, BankAccountActivity.class);
                BoughtProductsActivity.this.startActivity(myIntent2);
                break;

            case R.id.action_serverInfo:
                Intent serverIntent = new Intent(BoughtProductsActivity.this, ServerInfoActivity.class);
                BoughtProductsActivity.this.startActivity(serverIntent);
                break;

            case R.id.action_notifications:
                Intent notificationsIntent = new Intent(BoughtProductsActivity.this, NotificationListActivity.class);
                BoughtProductsActivity.this.startActivity(notificationsIntent);
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        BoughtProductsActivity.this.finish();
        return true;
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }*/
}
