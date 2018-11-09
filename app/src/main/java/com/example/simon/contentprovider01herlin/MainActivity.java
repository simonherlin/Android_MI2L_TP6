package com.example.simon.contentprovider01herlin;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {

    public static final int CREATE_CONTACT = 1;

    private EditText searchEditText;
    private ListView listView;
    private LinearLayout linearLayout;
    private Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_CONTACTS},1);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.contactsList);
        searchEditText = (EditText) findViewById(R.id.searchContactEditText);
        linearLayout = (LinearLayout) findViewById(R.id.addContactLayout);
        create = (Button) findViewById(R.id.create);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                intent.putExtra("name", searchEditText.getText().toString());
                startActivityForResult(intent, CREATE_CONTACT);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                displayContacts(searchEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void displayContacts(final String pattern) {
        ContentResolver contentResolver = getContentResolver();

        Cursor c = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?",
                new String[]{"%" + pattern.toLowerCase() + "%"},
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        );

        if (c != null && c.getCount() == 0) {
            listView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            SimpleCursorAdapter ca = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    c,
                    new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                    new int[]{android.R.id.text1},
                    0
            );

            linearLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(ca);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        searchEditText.setText("");
        displayContacts("");
    }

}

