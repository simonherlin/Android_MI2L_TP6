package com.example.simon.contentprovider01herlin;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class CreateActivity extends AppCompatActivity {

    Button valid;
    Button cancel;
    EditText name;
    EditText phone;

    public static final int VALID = 2;
    public static final int CANCEL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(CreateActivity.this,new String[]{Manifest.permission.WRITE_CONTACTS},1);
        setContentView(R.layout.activity_create);

        valid = findViewById(R.id.valid);
        name = findViewById(R.id.nameContact);
        phone = findViewById(R.id.phoneNumber);
        cancel = findViewById(R.id.cancel);

        name.setText(getIntent().getStringExtra("name"));

        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                int rawContactInsertIndex = ops.size();

                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name.getText().toString()) // Name of the person
                        .build());
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(
                                ContactsContract.Data.RAW_CONTACT_ID,   rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.getText().toString()) // Number of the person
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); // Type of mobile number
                try
                {
                    ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                }
                catch (RemoteException e)
                {
                    // error
                }
                catch (OperationApplicationException e)
                {
                    // error
                }
                Intent intent = new Intent(CreateActivity.this,MainActivity.class);
                intent.putExtra("name",name.getText().toString());
                setResult(VALID,intent);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateActivity.this,MainActivity.class);
                setResult(CANCEL,intent);
                finish();
            }
        });
    }
}
