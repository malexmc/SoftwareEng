package com.example.guilhermecortes.contactmanager;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.CheckBox;
import android.content.SharedPreferences;
import android.widget.CompoundButton;

public class privacySettings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String result = extras.getString("ADDRESS_TOGGLE");
        final Intent output = new Intent();

        if (extras != null) {
            String toggleValue = extras.getString("ADDRESS_TOGGLE");
        }
        setContentView(R.layout.activity_privacy_settings);

        final CheckBox checkBox1 = (CheckBox) findViewById(R.id.addressCheckBox);
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkBox1.isChecked() == false) {
                    output.putExtra("ADDRESS_TOGGLE", "false");
                }
                else {
                    output.putExtra("ADDRESS_TOGGLE", "true");
                }
                setResult(RESULT_OK, output);
            }

        });

    }

    public void onBackPressed() {
        finish();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK){
//            String stop = "STOP";
//        }
//    }

}
