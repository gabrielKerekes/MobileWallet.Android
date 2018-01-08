package com.mobilewallet.android;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mobilewallet.android.adapters.ProductAdapter;
import com.mobilewallet.android.pojos.Product;
import com.mobilewallet.android.services.ClientManager;
import com.mobilewallet.android.services.ToastMaker;

import static com.mobilewallet.android.services.MQTTClientInterface.BUY_TOPIC_REQUEST;
import static com.mobilewallet.android.services.MQTTClientInterface.BUY_TOPIC_RESPONSE;
import static com.mobilewallet.android.services.MQTTClientInterface.MULTIPLE_BUY_TOPIC_REQUEST;
import static com.mobilewallet.android.services.MQTTClientInterface.MULTIPLE_BUY_TOPIC_RESPONSE;

public class ProductActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ClientManager clientManager;

    private CardView buyButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        clientManager = ClientManager.getInstance();

        getSupportActionBar().setTitle("Products");

        mRecyclerView = (RecyclerView) findViewById(R.id.product_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ProductAdapter(clientManager.getAvailableProducts());

        mRecyclerView.setAdapter(mAdapter);

        buyButton = (CardView)findViewById(R.id.buyProductButton);

        //ClientManager.email = "abc@abc.sk"; // TODO: vymazat


        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClientManager.connected) {
                    ClientManager.productActivity = ProductActivity.this; // TODO: test this
                    System.out.println("Selected: " + Arrays.toString(mAdapter.getSelected().toArray()));
                    if (mAdapter.getSelected().size() > 0) {
                        if (mAdapter.getSelected().size() == 1) {
                            String buyTopic = mAdapter.getSelected().get(0).getTopicName();
                            int buyProduct = mAdapter.getSelected().get(0).getId();
                            int buyId = ClientManager.getAndUpdateBuyId();

                            List<Product> productToBuy = new ArrayList<Product>();
                            productToBuy.add(mAdapter.getSelected().get(0));
                            ClientManager.waitingForBuyEvaluation.put(buyId,productToBuy);

                            JSONObject message = new JSONObject();
                            try {
                                message.put("id",buyId);
                                message.put("accountNumber", ClientManager.accountNumber);
                                message.put("mail", ClientManager.email);
                                message.put("merchant",buyTopic);
                                message.put("product",buyProduct);
                                message.put("amount", 1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println(message.toString());
                            clientManager.publish(BUY_TOPIC_REQUEST+ClientManager.userName,message.toString());
                            clientManager.subscribe(BUY_TOPIC_RESPONSE+ClientManager.userName);
                        }
                        else {
                            int buyId = ClientManager.getAndUpdateBuyId();
                            List<Product> productsToBuy = new ArrayList<Product>();
                            JSONObject message = new JSONObject();
                            try {
                                message.put("id", buyId);
                                message.put("accountNumber", ClientManager.accountNumber);
                                message.put("mail", ClientManager.email);
                                JSONArray products = new JSONArray();
                                for (int i = 0; i < mAdapter.getSelected().size(); i++) {
                                    JSONObject oneProduct = new JSONObject();
                                    String buyTopic = mAdapter.getSelected().get(i).getTopicName();
                                    int buyProduct = mAdapter.getSelected().get(i).getId();
                                    oneProduct.put("merchant",buyTopic);
                                    oneProduct.put("product", buyProduct);
                                    oneProduct.put("amount",1);
                                    products.put(oneProduct);

                                    productsToBuy.add(mAdapter.getSelected().get(i));
                                }
                                message.put("products",products);
                                ClientManager.waitingForBuyEvaluation.put(buyId, productsToBuy);

                            } catch( JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println(message.toString());
                            clientManager.publish(MULTIPLE_BUY_TOPIC_REQUEST+ClientManager.userName,message.toString());
                            clientManager.subscribe(MULTIPLE_BUY_TOPIC_RESPONSE+ClientManager.userName);
                        }
                        //mAdapter.resetSelected(); // This is probabbly better commented
                    }
                } else {
                    ToastMaker.connectFirstToast(getApplicationContext());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_shops:
                Intent myIntent = new Intent(ProductActivity.this, TopicActivity.class);
                ProductActivity.this.startActivity(myIntent);
                break;

            case R.id.action_bankAccount:
                Intent myIntent2 = new Intent(ProductActivity.this, BankAccountActivity.class);
                ProductActivity.this.startActivity(myIntent2);
                break;

            case R.id.action_serverInfo:
                Intent serverIntent = new Intent(ProductActivity.this, ServerInfoActivity.class);
                ProductActivity.this.startActivity(serverIntent);
                break;

            case R.id.action_boughtProducts:
                Intent boughtProductsIntent = new Intent(ProductActivity.this, BoughtProductsActivity.class);
                ProductActivity.this.startActivity(boughtProductsIntent);
                break;

            case R.id.action_notifications:
                Intent notificationsIntent = new Intent(ProductActivity.this, NotificationListActivity.class);
                ProductActivity.this.startActivity(notificationsIntent);
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        ProductActivity.this.finish();
        return true;
    }

    public void createBoughtProductNotification(List<Product> products) {

        String title = "Transaction successfull!";
        StringBuilder contentBuilder = new StringBuilder();
        if (products.size() > 1) {
            for (Product product: products) {
                contentBuilder.append(product.getName());
                contentBuilder.append(" ");
            }
        }
        else {
            contentBuilder.append(products.get(0).getName());
        }

        Intent intent = new Intent(this,BoughtProductsActivity.class);
        PendingIntent pendingIntent =  PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_basket_grey600_18dp)
                        .setContentTitle(title)
                        .setContentText(contentBuilder.toString());

        mBuilder.setAutoCancel(true);

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(notificationSound);

        mBuilder.setContentIntent(pendingIntent);

        mNotificationManager.notify(1, mBuilder.build());
    }

    public void createUnsuccessfullTransactionNotification(String message) {

        String title = "Couldn't buy produt!";

        Intent intent = new Intent(this,ProductActivity.class);
        PendingIntent pendingIntent =  PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_basket_grey600_18dp)
                        .setContentTitle(title)
                        .setContentText(message);

        mBuilder.setAutoCancel(true);

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(notificationSound);

        mBuilder.setContentIntent(pendingIntent);

        mNotificationManager.notify(1, mBuilder.build());
    }

}
