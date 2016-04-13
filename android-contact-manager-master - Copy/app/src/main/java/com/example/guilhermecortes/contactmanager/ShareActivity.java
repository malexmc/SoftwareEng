package com.example.guilhermecortes.contactmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.Signature;
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
            Signature sig = null;

            KeyInfo mapboxKeyInfo = (KeyInfo) currentIntent.getSerializableExtra("KeyInfo");
            Boolean equivalent = false;

            try {
                sig = Signature.getInstance("MD5WithRSA");
                sig.initVerify(mapboxKeyInfo.getPubKey());
                sig.update(mapboxKeyInfo.getData());
                equivalent = sig.verify(mapboxKeyInfo.getSignatureBytes());
            }
            catch(Exception e){}


            if (equivalent) {
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

    public static String keyDigester(String key){
        MessageDigest messageDigest = null;
        String encryptedString = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        }

        catch(Exception e){}

        if(messageDigest != null) {
            messageDigest.update(key.getBytes());
            encryptedString = new String(messageDigest.digest());
        }
        return encryptedString;
    }

    public class KeyInfo implements Serializable {
        private PublicKey pubkey = null;
        private String data = null;
        private byte[] signatureBytes = null;

        public KeyInfo(PublicKey pubKey, String data, byte[] signatureBytes){
            this.pubkey = pubkey;
            this.data = data;
            this.signatureBytes = signatureBytes;
        }

        public PublicKey getPubKey (){
            return pubkey;
        }

        public byte[] getData (){
            return data.getBytes();
        }

        public byte[] getSignatureBytes (){
            return signatureBytes;
        }
    }

}
