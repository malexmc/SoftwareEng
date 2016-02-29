package com.example.guilhermecortes.contactmanager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends Activity {

    private EditText nameTxt, phoneTxt, emailTxt, addressTxt;
    public Boolean address_set = false;
    ImageView contactImageImgView;
    List<Contact> Contacts = new ArrayList<Contact>();
    ListView contactListView;
    Uri imageURI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTxt = (EditText) findViewById(R.id.txtName);
        phoneTxt = (EditText) findViewById(R.id.txtPhone);
        emailTxt = (EditText) findViewById(R.id.txtEmail);
        addressTxt = (EditText) findViewById(R.id.txtAddress);
        contactListView = (ListView) findViewById(R.id.listView);
        contactImageImgView = (ImageView) findViewById(R.id.imgViewContactImage);

        Intent currentIntent = getIntent();
        Bundle args = getIntent().getBundleExtra("args");
        if(args != null) {

            if (args.getString("Name") != null) {
                nameTxt.setText(args.getString("Name"));
            }

            if (args.getString("Phone") != null) {
                phoneTxt.setText(args.getString("Phone"));
            }

            if (args.getString("Address") != null) {
                addressTxt.setText(args.getString("Address"));
            }

            findViewById(R.id.btnAdd).setEnabled(true);
        }

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Creator");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tabContactList);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);


        final Button addBtn = (Button) findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contacts.add(new Contact(nameTxt.getText().toString(), phoneTxt.getText().toString(), emailTxt.getText().toString(), addressTxt.getText().toString(), imageURI));
                populateList();
                Toast.makeText(getApplicationContext(), nameTxt.getText().toString() +  " has been added to your Contacts!", Toast.LENGTH_SHORT).show();
            }
        });

        nameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            //habilitar o botao se o valor do campo for diferente de vazio
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                addBtn.setEnabled(!nameTxt.getText().toString().trim().isEmpty()); //trim para "cortar os espaços em branco"
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        contactImageImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Contact Image"), 1);
            }
        });

    }

    public void onActivityResult(int reqCode, int resCode, Intent data){
        if (resCode == RESULT_OK){
            if (reqCode == 1){
                imageURI = data.getData();
                contactImageImgView.setImageURI(data.getData());
            }

            if (reqCode == 2) {
                String stop = "STOP";
                String result = data.getExtras().getString("ADDRESS_TOGGLE");
                TextView temp = (TextView) findViewById(R.id.cAddress);
                if(Objects.equals(result, "true")){
                    temp.setTextColor(Color.BLUE);
                    temp.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                    temp.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            //Do nothing yet because our "Hyperlink" is not supposed to do anything

                        }
                    });
                }
                else{
                    temp.setPaintFlags(0);
                    temp.setTextColor(Color.BLACK);
                    //temp.setPaintFlags(~ Paint.UNDERLINE_TEXT_FLAG);
                }



            }
        }
    }

    private void populateList(){
        ArrayAdapter<Contact> adapter = new ContactListAdapter();
        contactListView.setAdapter(adapter);
    }

    public void onClickSettings(MenuItem item) {
        //String button_test;
        //button_test =((Button) item).getText().toString();
        //if (button_test.equals("Settings"))
        //{
            Intent intent1= new Intent(this,privacySettings.class);
            intent1.putExtra("ADDRESS_TOGGLE", "false");

            startActivityForResult(intent1, 2);
        //}
    }



    //add contact
//    private void addContact(String name, String phone, String email, String address){
//        Contacts.add(new Contact(name, phone, email, address));
//    }

    private class ContactListAdapter extends ArrayAdapter<Contact>{
        public ContactListAdapter(){
            super (MainActivity.this, R.layout.listview_item, Contacts);
        }

        //criar função para retornar o emelento do array
        @Override
        public View getView(int position, View view, ViewGroup parent){
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);

            privacySettings addressCheck = new privacySettings();

            Contact currentContact = Contacts.get(position);

            TextView name = (TextView) view.findViewById(R.id.contactName);
            name.setText(currentContact.get_name());
            TextView phone = (TextView) view.findViewById(R.id.phoneNumber);
            phone.setText(currentContact.get_phone());
            TextView email = (TextView) view.findViewById(R.id.emailAddress);
            email.setText(currentContact.get_email());
            TextView address = (TextView) view.findViewById(R.id.cAddress);
            address.setText(currentContact.get_address());
            ImageView ivContactImage = (ImageView) view.findViewById(R.id.ivContactImage);
            ivContactImage.setImageURI(currentContact.get_imageURI());

            return view;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK){
//            String stop = "STOP";
//        }
//    }

    /*public void onClickSettings(View view){
        String button_test;
        button_test =((Button) view).getText().toString();
        if (button_test.equals("Settings"))
        {
            Intent intent1= new Intent(this,privacySettings.class);
            startActivity(intent1);
        }
    }*/
}
