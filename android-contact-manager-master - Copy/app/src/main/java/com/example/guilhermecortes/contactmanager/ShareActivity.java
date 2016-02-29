package com.example.guilhermecortes.contactmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

public class ShareActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        //Get Intent
        Intent currentIntent = getIntent();
        String type = currentIntent.getType();
        String action = currentIntent.getAction();

        //If intent action is ADD_CONTACT
        if(action.equals("ADD_CONTACT")){

            String address_string = currentIntent.getStringExtra("Address");
            String phone_string = currentIntent.getStringExtra("Phone");
            String name_string = currentIntent.getStringExtra("Name");

            Bundle args = new Bundle();

            args.putString("Address", address_string);
            args.putString("Name", name_string);
            args.putString("Phone", phone_string);


            Context context = getApplicationContext();
            Intent startIntent = new Intent(context, MainActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.putExtra("args", args);
            context.startActivity(startIntent);

        }
    }

}
