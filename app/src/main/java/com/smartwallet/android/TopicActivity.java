package com.mobilewallet.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mobilewallet.android.adapters.ViewPagerAdapter;
import com.mobilewallet.android.fragments.AvailableTopicsFragment;
import com.mobilewallet.android.fragments.SubscribedTopicsFragment;
import com.mobilewallet.android.ui.SlidingTabLayout;

public class TopicActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Browse Shops","My Shops"};
    int Numboftabs = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs,getApplicationContext());

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        setTitle("Browse Shops");
                        break;

                    case 1:
                        setTitle("My Shops");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white);
            }
        });

        tabs.setCustomTabView(R.layout.custom_tab, 0);

        tabs.setViewPager(pager);

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
                Intent myIntent = new Intent(TopicActivity.this, ProductActivity.class);
                TopicActivity.this.startActivity(myIntent);
                break;

            case R.id.action_bankAccount:
                Intent myIntent2 = new Intent(TopicActivity.this, BankAccountActivity.class);
                TopicActivity.this.startActivity(myIntent2);
                break;

            case R.id.action_serverInfo:
                Intent serverIntent = new Intent(TopicActivity.this, ServerInfoActivity.class);
                TopicActivity.this.startActivity(serverIntent);
                break;

            case R.id.action_boughtProducts:
                Intent boughtProductsIntent = new Intent(TopicActivity.this, BoughtProductsActivity.class);
                TopicActivity.this.startActivity(boughtProductsIntent);
                break;

            case R.id.action_notifications:
                Intent notificationsIntent = new Intent(TopicActivity.this, NotificationListActivity.class);
                TopicActivity.this.startActivity(notificationsIntent);
                break;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    public void updateSubsbsribeFragment() {
        SubscribedTopicsFragment frag = (SubscribedTopicsFragment) adapter.instantiateItem(pager,1);
        frag.getAdapter().notifyDataSetChanged();
    }

    public void updateAvailableTopicsFragment() {
        AvailableTopicsFragment frag = (AvailableTopicsFragment) adapter.instantiateItem(pager,0);
        frag.getAdapter().notifyDataSetChanged();
    }

}
