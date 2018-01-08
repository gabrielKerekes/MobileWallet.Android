package com.mobilewallet.android.avengerstoken;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.mobilewallet.android.R;


public class PasswordWrite extends AppCompatActivity {

    EditText pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_write);

       // toGridCard = (Button) findViewById(R.id.button);
        //toGridCard.setOnClickListener(ocltoGridCard);

//        android.support.v7.app.ActionBar aBar = getSupportActionBar();
//        aBar.setTitle("Set secret");
//        aBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2B3742")));

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);

        pass = (EditText) findViewById(R.id.editText);
    }

    public  void setPassword(View view)
    {

        Intent data =new Intent();
        String password= String.valueOf(pass.getText());
        data.putExtra("password",password);
        setResult(RESULT_OK, data);
        finish();
    }


}
