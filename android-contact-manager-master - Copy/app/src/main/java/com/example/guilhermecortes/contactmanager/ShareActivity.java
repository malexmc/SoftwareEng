package com.example.guilhermecortes.contactmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.Serializable;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;


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

            // Use the sigVerifier function with the Data, encoded public key, and the signature bytes
            // from our Bestie, Mapbox, to make sure it really was Mapbox sending this stuff.
            //If Verifier certifies it was good data, extract it, else complain and quit.
            if (sigVerifier(/*currentIntent.getByteArrayExtra("Data")*/ "IAMMAPBOX".getBytes(),  currentIntent.getByteArrayExtra("PubKey"), currentIntent.getByteArrayExtra("SigBytes"))) {
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

            else{
                Toast toast = Toast.makeText(getApplicationContext(), "Unauthorized access of Contact List attempted", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }

    public Boolean sigVerifier(byte[] data, byte[] keyBytes, byte[] sigBytes){
        try {

            //Make key bytes into actual key
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);

            //make signature bytes into signature
            Signature newsig = Signature.getInstance("MD5WithRSA");
            newsig.initVerify(pubKey);
            newsig.update(data);

            //Validate Signature
            return newsig.verify(sigBytes);
        }

        catch(Exception e){}
        return false;
    }
}
