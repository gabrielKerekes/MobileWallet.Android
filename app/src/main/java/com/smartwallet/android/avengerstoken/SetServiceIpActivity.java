package com.smartwallet.android.avengerstoken;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.smartwallet.android.R;
import com.smartwallet.android.utils.ServiceIp;

public class SetServiceIpActivity extends AppCompatActivity
{
    EditText serviceIpEditText;
    Button saveIpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_service_ip);

        serviceIpEditText = (EditText) findViewById(R.id.serviceIpEditText);
        serviceIpEditText.setText(ServiceIp.GetIp(this));

        saveIpButton = (Button) findViewById(R.id.saveIpButton);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
    }

    public void saveIp(View view)
    {
        ServiceIp.SaveIp(this, serviceIpEditText.getText().toString());

        finish();
    }
}
