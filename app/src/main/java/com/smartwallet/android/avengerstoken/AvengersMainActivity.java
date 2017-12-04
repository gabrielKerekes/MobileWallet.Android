package com.smartwallet.android.avengerstoken;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.smartwallet.android.R;
import com.smartwallet.android.SuperwalletMainActivity;
import com.smartwallet.android.utils.UserManagement;


public class AvengersMainActivity extends AppCompatActivity{

    String initString= null;
    SharedPreferences sharedPreferences;
    Button regButt;
    Button setServiceIpButton;
    Button superwalletButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        regButt = (Button) findViewById(R.id.buttDevOnly);
        setServiceIpButton = (Button) findViewById(R.id.setServiceIpButton);
        superwalletButton = (Button) findViewById(R.id.superwalletButton);
        superwalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AvengersMainActivity.this, SuperwalletMainActivity.class);
                AvengersMainActivity.this.startActivity(myIntent);
            }
        });

        sharedPreferences = getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String reg_usr = sharedPreferences.getString("uname",null);

        if (reg_usr != null) {
            regButt.setVisibility(View.GONE);
            superwalletButton.setVisibility(View.VISIBLE);
        } else {
            regButt.setVisibility(View.VISIBLE);
            superwalletButton.setVisibility(View.GONE);
        }

        //new AppStatusService(this,reg_usr).execute();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
    }

    @Override
    protected void onResume(){
        super.onResume();

        String reg_usr = sharedPreferences.getString("uname", null);

        if (reg_usr != null) {
            regButt.setVisibility(View.GONE);
            superwalletButton.setVisibility(View.VISIBLE);
        } else {
            regButt.setVisibility(View.VISIBLE);
            superwalletButton.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (UserManagement.IsUserLoggedIn(this))
        {
            getMenuInflater().inflate(R.menu.menu_avengers_main, menu);
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_go_to_wallet:
                Intent myIntent = new Intent(this, SuperwalletMainActivity.class);
                startActivity(myIntent);
                finish();
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        return true;
    }


    //button pomocou ktoreho sa spusti grig karta
    public void startGridCardActivity(View view)
    {
            Intent gridcardintent = new Intent(AvengersMainActivity.this,GridCard.class);
            AvengersMainActivity.this.startActivity(gridcardintent);
    }

    //button pomocou ktoreho sa spusti oath
    public void startOfflineTokenActivity(View view)
    {
            Intent offlineToken = new Intent(AvengersMainActivity.this,OfflineToken.class);
            AvengersMainActivity.this.startActivity(offlineToken);
    }

    public void startSetServiceIpActivity(View view)
    {
        Intent setServiceIpActivity = new Intent(AvengersMainActivity.this, SetServiceIpActivity.class);
        AvengersMainActivity.this.startActivity(setServiceIpActivity);
    }

    public void startDeviceActivity(View view)
    {
        if(isOnline()) {
            Intent deviceActivity = new Intent(AvengersMainActivity.this, RegActivity.class);
            AvengersMainActivity.this.startActivity(deviceActivity);
        }else{
            Toast.makeText(getApplicationContext(), "Device is Offline !", Toast.LENGTH_LONG).show();
        }
    }

    public void setUp(String res){

        String reg_usr = sharedPreferences.getString("uname",null);

        if(res.equals("out")){
            Toast.makeText(getApplicationContext(), "Connection Error!", Toast.LENGTH_LONG).show();
        }else if(res.equals("err")){
            Toast.makeText(getApplicationContext(), "Not registered application!", Toast.LENGTH_LONG).show();
        }

    }

    //button na ukoncenie programu
    public void exit(View view)
    {
        finish();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private SharedPreferences getGcmPreferences(Context context){
        return getSharedPreferences(Globals.PREFS_NAME, Context.MODE_PRIVATE);
    }

}
